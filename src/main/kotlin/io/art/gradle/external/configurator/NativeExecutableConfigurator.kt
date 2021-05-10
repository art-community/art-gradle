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

package io.art.gradle.external.configurator

import io.art.gradle.common.constants.ART
import io.art.gradle.external.configuration.ExecutableConfiguration
import io.art.gradle.external.configuration.ExecutableConfiguration.NativeExecutableConfiguration
import io.art.gradle.external.constants.*
import io.art.gradle.external.plugin.externalPlugin
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.internal.os.OperatingSystem
import org.gradle.kotlin.dsl.support.unzipTo
import java.io.File
import java.nio.file.Paths

fun Project.configureNative() {
    with(externalPlugin.extension.executable) {
        tasks.findByPath(BUILD_EXECUTABLE_NATIVE_TASK)?.let { return }
        if (!nativeEnabled) return

        mainClass ?: return

        val buildNative = tasks.register(BUILD_EXECUTABLE_NATIVE_TASK, Exec::class.java) {
            group = ART

            val jarTask = tasks.getByName(BUILD_EXECUTABLE_JAR_TASK)
            dependsOn(jarTask)

            inputs.files(jarTask.outputs)

            val graalPaths = downloadGraal(native)
            directory.resolve(GRAAL).toFile().apply {
                mkdirs()
                GRAAL_CONFIGURATION_FILES.forEach { json ->
                    resolve(CONFIGURATION).apply {
                        mkdir()
                        val bytes = externalPlugin
                                .javaClass
                                .classLoader
                                .getResourceAsStream(GRAAL_BASE_RESOURCE_CONFIGURATION_PATH(native.graalJavaVersion, json))!!
                                .readBytes()
                        resolve(json).writeBytes(bytes)
                    }
                }
            }
            when {
                OperatingSystem.current().isWindows -> useWindowsBuilder(this@with, graalPaths)
                else -> useUnixBuilder(this@with, graalPaths)
            }

            native.buildConfigurator(this)
        }

        tasks.findByPath(RUN_EXECUTABLE_NATIVE_TASK)?.let { return@let }

        tasks.register(RUN_EXECUTABLE_NATIVE_TASK, Exec::class.java) {
            group = ART
            dependsOn(buildNative)

            inputs.files(buildNative.get().outputs)

            when {
                OperatingSystem.current().isWindows -> commandLine(directory.resolve("$executableName$DOT_EXE").toFile())
                else -> commandLine(directory.resolve(executableName).toFile())
            }

            native.runConfigurator(this)
        }
    }
}

private fun Project.downloadGraal(configuration: NativeExecutableConfiguration): GraalPaths {
    configuration.apply {
        val graalDirectory = graalDirectory?.toFile() ?: rootProject.buildDir.resolve(GRAAL)
        val archiveName = GRAAL_ARCHIVE_NAME(
                graalPlatform,
                graalJavaVersion,
                graalArchitecture,
                graalVersion
        )
        val archiveFile = graalDirectory.resolve(archiveName)
        val binariesDirectory = graalDirectory
                .resolve(GRAAL_UNPACKED_NAME(graalJavaVersion, graalVersion))
                .resolve(BIN)

        if (graalDirectory.exists() && binariesDirectory.resolve(GRAAL_NATIVE_IMAGE_EXECUTABLE).exists()) {
            return GraalPaths(
                    base = graalDirectory,
                    binary = binariesDirectory,
                    nativeImage = binariesDirectory.resolve(GRAAL_NATIVE_IMAGE_EXECUTABLE)
            )
        }

        if (!graalDirectory.mkdir()) throw GradleException()

        val url = GRAAL_DOWNLOAD_URL(archiveName, graalVersion)
        url.openStream().use { input -> archiveFile.outputStream().use { output -> input.transferTo(output) } }
        unzipTo(graalDirectory, archiveFile)
        archiveFile.delete()

        exec {
            commandLine(binariesDirectory.resolve(GRAAL_UPDATER_EXECUTABLE).absolutePath)
            args(GRAAL_UPDATE_NATIVE_IMAGE_ARGUMENTS)
        }

        return GraalPaths(
                base = graalDirectory,
                binary = binariesDirectory,
                nativeImage = binariesDirectory.resolve(GRAAL_NATIVE_IMAGE_EXECUTABLE)
        )
    }
}

private fun Exec.useWindowsBuilder(configuration: ExecutableConfiguration, paths: GraalPaths) = with(configuration) {
    val executablePath = directory.resolve(configuration.executableName).toFile()
    val graalPath = directory.resolve(GRAAL)
    val configurationPath = graalPath.resolve(CONFIGURATION)

    graalPath.resolve(GRAAL_WINDOWS_LAUNCH_SCRIPT_NAME).toFile().apply {
        val options = listOf(
                paths.nativeImage.absolutePath,
                JAR_OPTION, directory.resolve("$executableName$DOT_JAR").toAbsolutePath().toString(),
                executablePath.absolutePath,
                GRAAL_PROXY_CONFIGURATION_OPTION(configurationPath.resolve(GRAAL_PROXY_CONFIGURATION)),
                GRAAL_JNI_CONFIGURATION_OPTION(configurationPath.resolve(GRAAL_JNI_CONFIGURATION)),
                GRAAL_REFLECTION_CONFIGURATION_OPTION(configurationPath.resolve(GRAAL_REFLECTION_CONFIGURATION)),
                GRAAL_RESOURCE_CONFIGURATION_OPTION(configurationPath.resolve(GRAAL_RESOURCE_CONFIGURATION))
        ) + native.graalOptions

        val scriptPath = project.property(GRAAL_WINDOWS_VISUAL_STUDIO_VARS_SCRIPT_PROPERTY)?.let { property -> Paths.get(property as String) }
                ?: native.graalWindowsVcVarsPath
                ?: throw graalWindowsVSVarsPathIsEmpty()

        writeText(GRAAL_WINDOWS_LAUNCH_SCRIPT(graalPath, scriptPath, options))

        commandLine(POWERSHELL, absolutePath)
    }

    outputs.files(executablePath)
}

private fun Exec.useUnixBuilder(configuration: ExecutableConfiguration, paths: GraalPaths) = with(configuration) {
    val executablePath = directory.resolve(configuration.executableName).toFile()
    val graalPath = directory.resolve(GRAAL)
    val configurationPath = graalPath.resolve(CONFIGURATION)

    commandLine(paths.nativeImage.absolutePath)

    args(native.graalOptions)
    args(JAR_OPTION, directory.resolve("$executableName$DOT_JAR").toAbsolutePath().toString())
    args(executablePath.absolutePath)
    args(GRAAL_PROXY_CONFIGURATION_OPTION(configurationPath.resolve(GRAAL_PROXY_CONFIGURATION)))
    args(GRAAL_JNI_CONFIGURATION_OPTION(configurationPath.resolve(GRAAL_JNI_CONFIGURATION)))
    args(GRAAL_REFLECTION_CONFIGURATION_OPTION(configurationPath.resolve(GRAAL_REFLECTION_CONFIGURATION)))
    args(GRAAL_RESOURCE_CONFIGURATION_OPTION(configurationPath.resolve(GRAAL_RESOURCE_CONFIGURATION)))

    outputs.files(executablePath)
}

private data class GraalPaths(val base: File, val binary: File, val nativeImage: File)
