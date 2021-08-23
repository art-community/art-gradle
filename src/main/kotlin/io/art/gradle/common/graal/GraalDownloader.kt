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
import io.art.gradle.common.model.GraalPaths
import io.art.gradle.common.service.withLock
import org.gradle.api.Project
import org.gradle.internal.os.OperatingSystem
import java.io.File
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.TimeUnit.MILLISECONDS

private const val bufferSize = DEFAULT_BUFFER_SIZE * 2

fun Project.downloadGraal(configuration: NativeExecutableConfiguration): GraalPaths {
    val graalDirectory = configuration.graalDirectory?.toFile() ?: rootProject.buildDir.resolve(GRAAL)
    return supplyAsync {
        graalDirectory.resolve("$GRAAL$DOT_LOCK").toPath().withLock {
            var binariesDirectory = graalDirectory
                    .resolve(GRAAL_UNPACKED_NAME(configuration.graalJavaVersion, configuration.graalVersion))
                    .resolve(BIN)

            if (OperatingSystem.current().isMacOsX) {
                binariesDirectory =  binariesDirectory.parentFile.resolve(GRAAL_MAC_OS_BIN_PATH.toFile())
            }

            val nativeExecutable = binariesDirectory.resolve(GRAAL_NATIVE_IMAGE_EXECUTABLE)
            if (graalDirectory.exists() && nativeExecutable.exists()) {
                if (configuration.llvm) {
                    exec {
                        commandLine(binariesDirectory.resolve(GRAAL_UPDATER_EXECUTABLE).absolutePath)
                        args(GRAAL_UPDATE_LLVM_ARGUMENTS)
                    }
                }

                return@withLock GraalPaths(
                        base = graalDirectory,
                        binary = binariesDirectory,
                        nativeImage = nativeExecutable
                )
            }

            return@withLock processDownloading(configuration, graalDirectory)
        }
    }.get(GRAAL_DOWNLOAD_TIMEOUT.toMillis(), MILLISECONDS)
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
                val buffer = ByteArray(bufferSize)
                var read: Int
                while (input.read(buffer, 0, bufferSize).also { byte -> read = byte } >= 0) {
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

    var binariesDirectory = graalDirectory
            .resolve(GRAAL_UNPACKED_NAME(configuration.graalJavaVersion, configuration.graalVersion))
            .resolve(BIN)
    if (OperatingSystem.current().isMacOsX) {
        binariesDirectory =  binariesDirectory.parentFile.resolve(GRAAL_MAC_OS_BIN_PATH.toFile())
    }

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
