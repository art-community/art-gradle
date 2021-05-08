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
import io.art.gradle.external.constants.*
import io.art.gradle.external.plugin.externalPlugin
import org.gradle.api.JavaVersion.VERSION_1_9
import org.gradle.api.JavaVersion.current
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.JavaExec
import org.gradle.internal.os.OperatingSystem
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.support.unzipTo
import java.lang.Boolean.TRUE
import java.nio.file.Paths

fun Project.configureExecutable() {
    configureJar()
    configureNative()
}

fun Project.addEmbeddedConfiguration() {
    val implementation: Configuration = configurations.findByName(IMPLEMENTATION_CONFIGURATION_NAME) ?: return
    val embedded = configurations.maybeCreate(EMBEDDED_CONFIGURATION_NAME)
    if (!implementation.extendsFrom.contains(embedded)) {
        implementation.extendsFrom(embedded)
    }
}

private fun Project.configureJar() {
    with(externalPlugin.extension.executable) {
        tasks.findByPath(BUILD_EXECUTABLE_JAR_TASK)?.let { return }
        if (!jarEnabled) return

        mainClass ?: return

        val buildJar = tasks.register(BUILD_EXECUTABLE_JAR_TASK, Jar::class.java) {
            val buildTask = tasks.getByName(BUILD)
            val jarTask = tasks.getByName(JAR)
            dependsOn(buildTask)

            group = ART

            isZip64 = true

            duplicatesStrategy = jar.classedDuplicateStrategy

            manifest {
                attributes(mapOf(MAIN_CLASS_MANIFEST_ATTRIBUTE to mainClass))
                if (jar.multiRelease && current().isCompatibleWith(VERSION_1_9)) {
                    attributes(mapOf(MULTI_RELEASE_MANIFEST_ATTRIBUTE to TRUE.toString()))
                }
                attributes(jar.manifestAdditionalAttributes)
            }

            from(jarTask.outputs.files.map { if (it.isDirectory) it else zipTree(it) })
            from(configurations.getByName(EMBEDDED_CONFIGURATION_NAME).map { if (it.isDirectory) it else zipTree(it) })
            exclude(jar.exclusions)
            destinationDirectory.set(directory.toFile())

            archiveFileName.set("${this@with.executableName}.${archiveExtension.get()}")

            jar.buildConfigurator(this)
        }

        tasks.findByPath(RUN_EXECUTABLE_JAR_TASK)?.let { return }

        tasks.register(RUN_EXECUTABLE_JAR_TASK, JavaExec::class.java) {
            dependsOn(buildJar)
            classpath(buildJar.get().outputs.files)
            mainClass.set(this@with.mainClass)
            group = ART
            jar.runConfigurator(this)
        }
    }
}

private fun Project.configureNative() {
    with(externalPlugin.extension.executable) {
        tasks.findByPath(BUILD_EXECUTABLE_NATIVE_TASK)?.let { return }
        if (!nativeEnabled) return

        mainClass ?: return

        val buildNative = tasks.register(BUILD_EXECUTABLE_NATIVE_TASK, Exec::class.java) {
            val jarTask = tasks.getByName(BUILD_EXECUTABLE_JAR_TASK)
            dependsOn(jarTask)

            group = ART

            val graalDirectory = native.graalDirectory?.toFile() ?: temporaryDir.resolve(GRAAL)
            val archiveName = GRAAL_VM_ARCHIVE_NAME(
                    native.graalPlatform,
                    native.graalJavaVersion,
                    native.graalArchitecture,
                    native.graalVersion.version
            )
            val archiveFile = graalDirectory.resolve(archiveName)
            val binariesDirectory = graalDirectory
                    .resolve(GRAAL_VM_UNPACKED_NAME(native.graalJavaVersion, native.graalVersion.version))
                    .resolve("bin")

            doFirst {
                if (graalDirectory.exists()) return@doFirst
                if (!graalDirectory.mkdir()) return@doFirst
                val url = GRAAL_VM_DOWNLOAD_URL(archiveName, native.graalVersion.version)
                url.openStream().use { input -> archiveFile.outputStream().use { output -> input.transferTo(output) } }
                unzipTo(graalDirectory, archiveFile)
                archiveFile.delete()

                exec {
                    when {
                        OperatingSystem.current().isWindows -> commandLine(binariesDirectory.resolve("gu.cmd").absolutePath)
                        else -> commandLine(binariesDirectory.resolve("gu").absolutePath)
                    }

                    args("install", "native-image")
                }
            }

            when {
                OperatingSystem.current().isWindows -> {
                    val graalPath = directory.resolve("graal").apply {
                        toFile().mkdirs()
                        val configurations = listOf(
                                "jni-config.json",
                                "proxy-config.json",
                                "reflect-config.json",
                                "resource-config.json"
                        )
                        configurations.forEach { json ->
                            val configurationPath = resolve("configuration")
                            configurationPath.toFile().mkdirs()
                            configurationPath.resolve(json)
                                    .toFile()
                                    .writeBytes(externalPlugin.javaClass.classLoader.getResourceAsStream("external/graal/jdk-11/$json")!!.readBytes())
                        }
                    }
                    val options = listOf(
                            binariesDirectory.resolve("native-image.cmd").absolutePath,
                            "-jar", directory.resolve("$executableName.jar").toAbsolutePath().toString(),
                            directory.resolve(executableName).toAbsolutePath().toString(),
                            """-H:DynamicProxyConfigurationFiles=${graalPath.resolve("configuration").resolve("proxy-config.json").toAbsolutePath()}""",
                            """-H:JNIConfigurationFiles=${graalPath.resolve("configuration").resolve("jni-config.json").toAbsolutePath()}""",
                            """-H:ReflectionConfigurationFiles=${graalPath.resolve("configuration").resolve("reflect-config.json").toAbsolutePath()}""",
                            """-H:ResourceConfigurationFiles=${graalPath.resolve("configuration").resolve("resource-config.json").toAbsolutePath()}""",
                    ) + GRAAL_MANDATORY_OPTIONS + native.graalAddtionalOptions

                    val windowsPath = Paths.get("D:/Development/Microsoft Visual Studio/VC/Auxiliary/Build/vcvars64.bat")
                    graalPath.resolve("native-build-windows.ps1").apply {
                        toFile().writeText(GRAAL_WINDOWS_LAUNCH_SCRIPT(windowsPath, options))
                        commandLine("powershell", toFile().absolutePath)
                    }
                }
                else -> commandLine(binariesDirectory.resolve("native-image").absolutePath)
            }
        }

        tasks.findByPath(RUN_EXECUTABLE_NATIVE_TASK)?.let { return }

        tasks.register(RUN_EXECUTABLE_NATIVE_TASK, Exec::class.java) {
            dependsOn(buildNative)
            group = ART
            commandLine(directory.resolve(executableName))
            when {
                OperatingSystem.current().isWindows -> args(directory.resolve("$executableName.exe"))
                else -> args(directory.resolve(executableName))
            }
        }
    }
}
