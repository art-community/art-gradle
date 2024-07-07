/*
 * ART
 *
 * Copyright 2019-2022 ART
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.art.gradle.common.graal

import io.art.gradle.common.configuration.NativeExecutableConfiguration
import io.art.gradle.common.constants.*
import io.art.gradle.common.constants.GraalPlatformName.*
import io.art.gradle.common.model.GraalPaths
import io.art.gradle.common.service.withLock
import org.gradle.api.Project
import org.gradle.internal.os.OperatingSystem
import java.io.File
import java.net.URI
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.TimeUnit.MILLISECONDS

private const val bufferSize = DEFAULT_BUFFER_SIZE * 2

fun Project.downloadGraal(configuration: NativeExecutableConfiguration): GraalPaths {
    configuration.graalLocalDirectory?.takeIf { path -> path.toFile().exists() }?.let { directory ->
        var binary = directory.toFile().resolve(BIN)
        if (OperatingSystem.current().isMacOsX) binary = binary.parentFile.resolve(GRAAL_MAC_OS_BIN_PATH.toFile())
        var nativeImage = binary.resolve(GRAAL_NATIVE_IMAGE_EXECUTABLE).apply { setExecutable(true) }
        if (configuration.wsl) nativeImage = binary.resolve(GRAAL_UNIX_NATIVE_IMAGE).apply { setExecutable(true) }
        return GraalPaths(base = directory.toFile(), binary = binary, nativeImage = nativeImage)
    }
    val graalDirectory = configuration.graalDownloadingDirectory?.toFile() ?: rootProject.layout.buildDirectory.file(GRAAL).get().asFile
    return supplyAsync {
        graalDirectory.resolve("$GRAAL$DOT_LOCK").toPath().withLock {
            val graalContentDirectory = graalDirectory.listFiles()?.first()
            var binariesDirectory = graalContentDirectory?.resolve(BIN)
            if (OperatingSystem.current().isMacOsX) binariesDirectory = binariesDirectory?.parentFile?.resolve(GRAAL_MAC_OS_BIN_PATH.toFile())
            var nativeExecutable = binariesDirectory?.resolve(GRAAL_NATIVE_IMAGE_EXECUTABLE)?.apply { setExecutable(true) }
            if (configuration.wsl) nativeExecutable = binariesDirectory?.resolve(GRAAL_UNIX_NATIVE_IMAGE)?.apply { setExecutable(true) }
            if (graalContentDirectory?.exists() == true && nativeExecutable?.exists() == true) {
                return@withLock GraalPaths(base = graalContentDirectory, binary = binariesDirectory!!, nativeImage = nativeExecutable)
            }

            return@withLock processDownloading(configuration, graalDirectory)
        }
    }.get(GRAAL_DOWNLOAD_TIMEOUT.toMillis(), MILLISECONDS)
}

private fun Project.processDownloading(configuration: NativeExecutableConfiguration, graalDirectory: File): GraalPaths {
    var resultGraalDirectory = graalDirectory

    val archiveName = GRAAL_ARCHIVE_NAME(
        if (configuration.wsl) LINUX else configuration.graalPlatform,
        configuration.graalPrefix,
        configuration.graalArchitecture,
    )

    val archiveFile = resultGraalDirectory.resolve(archiveName)

    if (!resultGraalDirectory.exists()) {
        resultGraalDirectory.mkdirs()
    }

    if (!archiveFile.exists()) {
        (configuration.graalUrl?.let(::URI)?.let(URI::toURL) ?: GRAAL_DOWNLOAD_URL(configuration.graalVersion, archiveName)).openStream().use { input ->
            archiveFile.outputStream().use { output ->
                val buffer = ByteArray(bufferSize)
                var read: Int
                while (input.read(buffer, 0, bufferSize).also { byte -> read = byte } >= 0) {
                    output.write(buffer, 0, read)
                }
            }
        }
    }

    when {
        configuration.wsl -> exec {
            commandLine(
                *bashCommand(
                    TAR, TAR_EXTRACT_ZIP_OPTIONS, archiveFile.absoluteFile.absolutePath.wsl(), TAR_DIRECTORY_OPTION, resultGraalDirectory.absoluteFile.absolutePath.wsl()
                )
            )
        }

        configuration.graalPlatform == WINDOWS -> copy {
            from(zipTree(archiveFile))
            into(resultGraalDirectory)
        }

        configuration.graalPlatform == LINUX || configuration.graalPlatform == DARWIN -> exec {
            commandLine(TAR)
            args(TAR_EXTRACT_ZIP_OPTIONS, archiveFile.absoluteFile)
            args(TAR_DIRECTORY_OPTION, resultGraalDirectory.absoluteFile)
        }
    }

    archiveFile.delete()

    resultGraalDirectory = resultGraalDirectory.listFiles()!!.first()
    var binariesDirectory = resultGraalDirectory.resolve(BIN)

    if (OperatingSystem.current().isMacOsX) binariesDirectory = binariesDirectory.parentFile.resolve(GRAAL_MAC_OS_BIN_PATH.toFile())
    var nativeImage = binariesDirectory.resolve(GRAAL_NATIVE_IMAGE_EXECUTABLE).apply { setExecutable(true) }
    if (configuration.wsl) nativeImage = binariesDirectory.resolve(GRAAL_UNIX_NATIVE_IMAGE).apply { setExecutable(true) }
    return GraalPaths(
        base = resultGraalDirectory, binary = binariesDirectory, nativeImage = nativeImage
    )
}
