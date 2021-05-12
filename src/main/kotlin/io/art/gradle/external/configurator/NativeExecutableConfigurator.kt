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
import io.art.gradle.common.constants.EMPTY_STRING
import io.art.gradle.common.constants.JAVA
import io.art.gradle.common.constants.SPACE
import io.art.gradle.common.logger.warning
import io.art.gradle.external.configuration.ExecutableConfiguration
import io.art.gradle.external.constants.*
import io.art.gradle.external.constants.GraalAgentOutputMode.MERGE
import io.art.gradle.external.constants.GraalAgentOutputMode.OVERWRITE
import io.art.gradle.external.constants.GraalJavaVersion.JAVA_8
import io.art.gradle.external.constants.GraalPlatformName.DARWIN
import io.art.gradle.external.graal.downloadGraal
import io.art.gradle.external.model.GraalPaths
import io.art.gradle.external.plugin.externalPlugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.JavaExec
import org.gradle.internal.os.OperatingSystem
import java.nio.file.Path
import java.nio.file.Paths

fun Project.configureNative() {
    with(externalPlugin.extension.executable) {
        if (!nativeEnabled) return
        mainClass ?: return

        if (native.graalJavaVersion == JAVA_8 && native.graalPlatform == DARWIN) {
            warning(GRAAL_VM_JDK_8_DARWIN_WARING)
            return
        }

        if (native.enableAgent) {
            configureAgent(this)
        }

        tasks.findByPath(BUILD_EXECUTABLE_NATIVE_TASK)?.let { return }

        val buildNative = tasks.register(BUILD_EXECUTABLE_NATIVE_TASK, Exec::class.java) {
            group = ART

            val jarTask = tasks.getByName(BUILD_EXECUTABLE_JAR_TASK)

            dependsOn(jarTask)

            if (native.runAgentBeforeBuild) {
                dependsOn(RUN_WITH_NATIVE_IMAGE_AGENT)
            }

            inputs.files(jarTask.outputs.files)

            doFirst {
                val graalPaths = downloadGraal(native)
                val configurations = native.graalConfigurationDirectory
                        ?: directory.resolve(GRAAL).resolve(CONFIGURATION)
                extractGraalConfigurations(configurations)

                when {
                    OperatingSystem.current().isWindows -> useWindowsBuilder(this@with, graalPaths)
                    else -> useUnixBuilder(this@with, graalPaths)
                }
            }

            native.buildConfigurator(this)
        }

        tasks.findByPath(RUN_EXECUTABLE_NATIVE_TASK)?.let { return@let }

        tasks.register(RUN_EXECUTABLE_NATIVE_TASK, Exec::class.java) {
            group = ART
            dependsOn(buildNative)

            when {
                OperatingSystem.current().isWindows -> commandLine(directory.resolve("$executableName$DOT_EXE").toFile())
                else -> commandLine(directory.resolve(executableName).toFile())
            }

            native.runConfigurator(this)
        }
    }
}

private fun Project.configureAgent(executableConfiguration: ExecutableConfiguration) = with(executableConfiguration) {
    tasks.findByPath(RUN_WITH_NATIVE_IMAGE_AGENT)?.let { return@with }

    tasks.register(RUN_WITH_NATIVE_IMAGE_AGENT, JavaExec::class.java) {
        group = ART
        val jarTask = tasks.getByName(BUILD_EXECUTABLE_JAR_TASK)
        dependsOn(jarTask)
        inputs.files(jarTask.outputs.files)

        val graalPaths = downloadGraal(native)
        val outputPath = native.agentConfiguration.configurationPath ?: directory.resolve(GRAAL).resolve(CONFIGURATION)
        extractGraalConfigurations(outputPath)

        mainClass.set(native.agentConfiguration.executableClass ?: this@with.mainClass)

        executable(graalPaths.binary.resolve(JAVA).apply { setExecutable(true) }.absolutePath)
        classpath(jarTask.outputs.files)
        workingDir(directory.toFile())

        native.agentConfiguration.apply {
            val agentArgument = "$GRAAL_NATIVE_IMAGE_AGENT_OPTION="
            val options = mutableListOf<String>()
            options += when (outputMode) {
                OVERWRITE -> GRAAL_AGENT_OUTPUT_DIR_OPTION(outputPath)
                MERGE -> GRAAL_AGENT_MERGE_DIR_OPTION(outputPath)
            }

            configurationWritePeriod?.let { period -> options += ",${GRAAL_AGENT_WRITE_PERIOD_OPTION(period.seconds)}" }
            configurationWriteInitialDelay?.let { delay -> options += ",${GRAAL_AGENT_WRITE_INITIAL_DELAY_OPTION(delay.seconds)}" }

            val accessFilter = accessFilter ?: outputPath.resolve(GRAAL_ACCESS_FILTER_CONFIGURATION)
            val callerFilter = callerFilter ?: outputPath.resolve(GRAAL_CALLER_FILTER_CONFIGURATION)
            accessFilter.let { path -> options += ",${GRAAL_AGENT_ACCESS_FILTER_OPTION(path)}" }
            callerFilter.let { path -> options += ",${GRAAL_AGENT_CALLER_FILTER_OPTION(path)}" }

            agentOptions.forEach { option -> options += ",$option" }

            jvmArgs = listOf(agentArgument + agentOptionsReplacer(options).joinToString(EMPTY_STRING))

            runConfigurator(this@register)
        }
    }
}

private fun ExecutableConfiguration.extractGraalConfigurations(path: Path) {
    GRAAL_CONFIGURATION_FILES.forEach { json ->
        path.toFile().apply {
            if (!exists()) {
                mkdirs()
            }

            if (resolve(json).exists()) {
                return@forEach
            }

            val bytes = externalPlugin
                    .javaClass
                    .classLoader
                    .getResourceAsStream(GRAAL_BASE_RESOURCE_CONFIGURATION_PATH(native.graalJavaVersion, json))!!
                    .readBytes()

            resolve(json).writeBytes(bytes)
        }
    }
}

private fun Exec.useWindowsBuilder(configuration: ExecutableConfiguration, paths: GraalPaths) = with(configuration) {
    val executablePath = directory.resolve(configuration.executableName).toFile()
    val graalPath = directory.resolve(GRAAL)
    val configurationPath = graalPath.resolve(CONFIGURATION)

    graalPath.resolve(GRAAL_WINDOWS_LAUNCH_SCRIPT_NAME).toFile().apply {
        val executable = paths.nativeImage.absolutePath

        val optionsByProperty = (project.findProperty(GRAAL_OPTIONS_PROPERTY) as? String)?.split(SPACE) ?: emptyList()

        val defaultOptions = listOf(
                JAR_OPTION, directory.resolve("$executableName$DOT_JAR").toAbsolutePath().toString(),
                executablePath.absolutePath,
                GRAAL_CONFIGURATIONS_PATH_OPTION(configurationPath)
        )

        val options = defaultOptions + native.graalOptions + optionsByProperty

        val scriptPath = project.findProperty(GRAAL_WINDOWS_VISUAL_STUDIO_VARS_PROPERTY)?.let { property -> Paths.get(property as String) }
                ?: native.graalWindowsVcVarsPath
                ?: throw graalWindowsVSVarsPathIsEmpty()

        if (!scriptPath.toFile().exists()) {
            throw graalWindowsVSVarsPathIsEmpty()
        }

        writeText(GRAAL_WINDOWS_LAUNCH_SCRIPT(graalPath, scriptPath, listOf(executable) + native.graalOptionsReplacer(options)))

        commandLine(POWERSHELL, absolutePath)
    }
}

private fun Exec.useUnixBuilder(configuration: ExecutableConfiguration, paths: GraalPaths) = with(configuration) {
    val executablePath = directory.resolve(configuration.executableName).toFile()
    val graalPath = directory.resolve(GRAAL)
    val configurationPath = graalPath.resolve(CONFIGURATION)

    commandLine(paths.nativeImage.absolutePath)

    val optionsByProperty = (project.findProperty(GRAAL_OPTIONS_PROPERTY) as? String)?.split(SPACE) ?: emptyList()

    val defaultOptions = listOf(
            JAR_OPTION, directory.resolve("$executableName$DOT_JAR").toAbsolutePath().toString(),
            executablePath.absolutePath,
            GRAAL_CONFIGURATIONS_PATH_OPTION(configurationPath))

    val options = defaultOptions + native.graalOptions + optionsByProperty

    args(native.graalOptionsReplacer(options))
}
