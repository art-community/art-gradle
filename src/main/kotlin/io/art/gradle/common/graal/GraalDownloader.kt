/*
 * ART
 *
 * Copyright 2019-2021 ART
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
import io.art.gradle.common.logger.additional
import io.art.gradle.common.model.GraalPaths
import org.gradle.api.Project
import java.io.File
import java.lang.Thread.interrupted
import java.nio.channels.FileChannel.open
import java.nio.file.StandardOpenOption.READ
import java.nio.file.StandardOpenOption.WRITE
import java.time.LocalTime.now
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private val LOCK = ReentrantLock()

fun Project.downloadGraal(configuration: NativeExecutableConfiguration): GraalPaths = LOCK.withLock {
    val graalDirectory = configuration.graalDirectory?.toFile() ?: rootProject.buildDir.resolve(GRAAL)
    val lockFile = graalDirectory.apply { if (!exists()) mkdirs() }.resolve("$GRAAL$DOT_LOCK").apply { deleteOnExit() }
    val channel = open(lockFile.toPath(), READ, WRITE)
    val lock = channel.lock()
    val time = now().plus(GRAAL_DOWNLOAD_TIMEOUT)
    try {
        while (lockFile.exists() && !lock.isValid && !interrupted()) {
            if (now().isAfter(time)) {
                throw lockTimeout()
            }
        }

        if (!lockFile.createNewFile() || !lock.isValid) {
            throw lockCreation()
        }

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

        return processDownloading(configuration, graalDirectory)
    } finally {
        lock.release()
        channel.close()
        lockFile.delete()
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
        additional("Downloading GraalVM...")
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
