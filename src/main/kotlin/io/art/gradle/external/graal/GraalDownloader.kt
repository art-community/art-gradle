package io.art.gradle.external.graal

import io.art.gradle.external.configuration.NativeExecutableConfiguration
import io.art.gradle.external.constants.*
import io.art.gradle.external.constants.GraalPlatformName.*
import io.art.gradle.external.model.GraalPaths
import org.gradle.api.Project
import java.io.File
import java.nio.channels.FileChannel.open
import java.nio.channels.FileLock
import java.nio.file.StandardOpenOption.SYNC

fun Project.downloadGraal(configuration: NativeExecutableConfiguration): GraalPaths {
    val graalDirectory = configuration.graalDirectory?.toFile() ?: rootProject.buildDir.resolve(GRAAL)
    val binariesDirectory = graalDirectory
            .resolve(GRAAL_UNPACKED_NAME(configuration.graalJavaVersion, configuration.graalVersion))
            .walkTopDown()
            .find { file -> file.name == GRAAL_UPDATER_EXECUTABLE }
            ?.parentFile

    if (graalDirectory.exists() && binariesDirectory?.resolve(GRAAL_NATIVE_IMAGE_EXECUTABLE)?.exists() == true) {
        if (configuration.llvm) {
            exec {
                commandLine(binariesDirectory.resolve(GRAAL_UPDATER_EXECUTABLE).absolutePath)
                args(GRAAL_UPDATE_LLVM_ARGUMENTS)
            }
        }

        return GraalPaths(
                base = graalDirectory,
                binary = binariesDirectory,
                nativeImage = binariesDirectory.resolve(GRAAL_NATIVE_IMAGE_EXECUTABLE)
        )
    }

    open(graalDirectory.resolve("$GRAAL$DOT_LOCK").toPath(), SYNC).use { channel ->
        var lock: FileLock? = null
        try {
            do {
                lock = runCatching { channel.tryLock() }.getOrNull()
            } while (lock?.isValid != true)
            return processDownloading(configuration, graalDirectory)
        } finally {
            lock?.release()
        }
    }
}

private fun Project.processDownloading(configuration: NativeExecutableConfiguration, graalDirectory: File): GraalPaths {
    val archiveName = GRAAL_ARCHIVE_NAME(
            configuration.graalPlatform,
            configuration.graalJavaVersion,
            configuration.graalArchitecture,
            configuration.graalVersion
    )

    val archiveFile = graalDirectory.resolve(archiveName)

    if (!graalDirectory.exists()) {
        graalDirectory.mkdirs()
    }

    if (!archiveFile.exists()) {
        GRAAL_DOWNLOAD_URL(archiveName, configuration.graalVersion).openStream().use { input ->
            archiveFile.outputStream().use { output ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE * 2)
                var read: Int
                while (input.read(buffer, 0, DEFAULT_BUFFER_SIZE * 2).also { read = it } >= 0) {
                    output.write(buffer, 0, read)
                }
            }
        }
    }

    when (configuration.graalPlatform) {
        WINDOWS -> copy {
            from(zipTree(archiveFile))
            into(graalDirectory)
        }
        LINUX, DARWIN -> exec {
            commandLine(TAR)
            args(TAR_EXTRACT_ZIP_OPTIONS, archiveFile.absoluteFile)
            args(TAR_DIRECTORY_OPTION, graalDirectory.absoluteFile)
        }
    }

    archiveFile.delete()

    val binariesDirectory = graalDirectory
            .resolve(GRAAL_UNPACKED_NAME(configuration.graalJavaVersion, configuration.graalVersion))
            .walkTopDown()
            .find { file -> file.name == GRAAL_UPDATER_EXECUTABLE }
            ?.parentFile ?: throw unableToFindGraalUpdater()

    exec {
        commandLine(binariesDirectory.resolve(GRAAL_UPDATER_EXECUTABLE).apply { setExecutable(true) }.absolutePath)
        args(GRAAL_UPDATE_NATIVE_IMAGE_ARGUMENTS)
    }

    return GraalPaths(
            base = graalDirectory,
            binary = binariesDirectory,
            nativeImage = binariesDirectory.resolve(GRAAL_NATIVE_IMAGE_EXECUTABLE).apply { setExecutable(true) }
    )
}
