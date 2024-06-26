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

package io.art.gradle.common.configurator

import io.art.gradle.common.configuration.NativeExecutableConfiguration
import io.art.gradle.common.constants.*
import io.art.gradle.common.constants.GraalAgentOutputMode.MERGE
import io.art.gradle.common.constants.GraalAgentOutputMode.OVERWRITE
import io.art.gradle.common.constants.GraalPlatformName.DARWIN
import io.art.gradle.common.graal.downloadGraal
import io.art.gradle.common.logger.log
import io.art.gradle.common.model.GraalPaths
import io.art.gradle.common.service.touch
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.JavaExec
import org.gradle.internal.os.OperatingSystem
import java.lang.System.getenv
import java.nio.file.Path
import java.nio.file.Paths

data class NativeExecutableCreationConfiguration(
        val configuration: NativeExecutableConfiguration,
        val runTask: String,
        val buildTask: String,
        val buildJarTask: String,
        val runAgentTask: String,
        val mainClass: String?,
        val executable: String,
        val directory: Path,
)

fun Project.configureNative(executableConfiguration: NativeExecutableCreationConfiguration) {
    val native = executableConfiguration.configuration

    if (native.enableAgent) {
        configureAgent(executableConfiguration)
    }

    tasks.findByPath(executableConfiguration.buildTask)?.let { return }

    val executable = executableConfiguration.executable
    val buildNative = tasks.register(executableConfiguration.buildTask, Exec::class.java) {
        group = NATIVE

        val jarTask = tasks.getByName(executableConfiguration.buildJarTask)

        dependsOn(jarTask)

        if (native.runAgentBeforeBuild) {
            dependsOn(executableConfiguration.runAgentTask)
        }

        inputs.files(jarTask.outputs.files)

        doFirst {
            val graalPaths = downloadGraal(native)
            when {
                !native.wsl && OperatingSystem.current().isWindows -> useWindowsBuilder(executableConfiguration, graalPaths, executable)
                else -> useUnixBuilder(executableConfiguration, graalPaths, executable)
            }
        }

        native.buildConfigurator(this)
    }

    tasks.findByPath(executableConfiguration.runTask)?.let { return@let }

    tasks.register(executableConfiguration.runTask, Exec::class.java) {
        group = NATIVE
        dependsOn(buildNative)

        val directory = executableConfiguration.directory
        when {
            !native.wsl && OperatingSystem.current().isWindows -> commandLine(directory.resolve("$executable$DOT_EXE").toFile())
            else -> commandLine(*bashCommand(directory.resolve(executable).toFile().absolutePath.wsl()))
        }

        native.runConfigurator(this)
    }
}

private fun Project.configureAgent(executableConfiguration: NativeExecutableCreationConfiguration) = with(executableConfiguration) {
    tasks.findByPath(runAgentTask)?.let { return@with }

    tasks.register(runAgentTask, JavaExec::class.java) {
        group = NATIVE
        val native = executableConfiguration.configuration
        val jarTask = tasks.getByName(buildJarTask)
        dependsOn(jarTask)
        inputs.files(jarTask.outputs.files)
        this@with.mainClass?.let { main -> mainClass.set(native.agentConfiguration.executableClass ?: main) }
        classpath(jarTask.outputs.files)
        workingDir(directory.toFile())

        doFirst {
            val graalPaths = downloadGraal(native)
            val configurationPath = native.agentConfiguration.configurationPath
                    ?: directory.resolve(GRAAL).resolve(CONFIGURATION)

            configurationPath.touch()


            executable(graalPaths.binary.resolve(JAVA).apply { setExecutable(true) }.absolutePath)

            native.agentConfiguration.apply {
                val agentArgument = "$GRAAL_NATIVE_IMAGE_AGENT_OPTION="
                val options = mutableListOf<String>()
                options += when (outputMode) {
                    OVERWRITE -> GRAAL_AGENT_OUTPUT_DIR_OPTION(configurationPath)
                    MERGE -> GRAAL_AGENT_MERGE_DIR_OPTION(configurationPath)
                }

                configurationWritePeriod?.let { period -> options += ",${GRAAL_AGENT_WRITE_PERIOD_OPTION(period.seconds)}" }
                configurationWriteInitialDelay?.let { delay -> options += ",${GRAAL_AGENT_WRITE_INITIAL_DELAY_OPTION(delay.seconds)}" }
                accessFilter?.let { path -> options += ",${GRAAL_AGENT_ACCESS_FILTER_OPTION(path)}" }
                callerFilter?.let { path -> options += ",${GRAAL_AGENT_CALLER_FILTER_OPTION(path)}" }

                agentOptions.forEach { option -> options += ",$option" }

                jvmArgs = listOf(agentArgument + agentOptionsReplacer(options).joinToString(EMPTY_STRING))

                runConfigurator(this@register)
            }
        }
    }
}

private fun Exec.useWindowsBuilder(configuration: NativeExecutableCreationConfiguration, paths: GraalPaths, executableName: String) = with(configuration) {
    val executablePath = directory.resolve(executableName).toFile()
    val graalPath = directory.resolve(GRAAL).touch()

    graalPath.resolve(GRAAL_WINDOWS_LAUNCH_SCRIPT_NAME).toFile().apply {
        val executable = paths.nativeImage.absolutePath
        val configurationPath = graalPath.resolve(CONFIGURATION)
        val native = configuration.configuration

        val optionsByProperty = (project.findProperty(GRAAL_OPTIONS_PROPERTY) as? String)?.split(SPACE) ?: emptyList()

        val defaultOptions = listOf(
                JAR_OPTION, directory.resolve("${configuration.executable}$DOT_JAR").toAbsolutePath().toString(),
                executablePath.absolutePath,
                GRAAL_CONFIGURATIONS_PATH_OPTION(configurationPath.toAbsolutePath().toString())
        )

        val systemProperties = native
                .graalSystemProperties
                .map { entry -> SYSTEM_PROPERTY(entry.key, entry.value) } + SYSTEM_PROPERTY(GRAAL_WORKING_PATH_PROPERTY, directory.toFile().absolutePath)
        val options = (defaultOptions + native.graalOptions + systemProperties + optionsByProperty).toMutableList()

        val scriptPath = project
                .findProperty(GRAAL_WINDOWS_VISUAL_STUDIO_VARS_PROPERTY)
                ?.let { property -> Paths.get(property as String) }
                ?: native.graalWindowsVcVarsPath
                ?: getenv()[GRAAL_WINDOWS_VISUAL_STUDIO_VARS_ENVIRONMENT]?.let(Paths::get)
                ?: throw graalWindowsVSVarsPathIsEmpty()

        if (!scriptPath.toFile().exists()) {
            throw graalWindowsVSVarsPathIsEmpty()
        }

        writeText(GRAAL_WINDOWS_LAUNCH_SCRIPT(graalPath, scriptPath, listOf(executable) + native.graalOptionsReplacer(options)))

        commandLine(POWERSHELL, absolutePath)
    }
}

private fun Exec.useUnixBuilder(configuration: NativeExecutableCreationConfiguration, paths: GraalPaths, executableName: String) = with(configuration) {
    val executablePath = directory.resolve(executableName).toFile()
    val graalPath = directory.resolve(GRAAL)
    val configurationPath = graalPath.resolve(CONFIGURATION)
    val native = configuration.configuration

    val optionsByProperty = (project.findProperty(GRAAL_OPTIONS_PROPERTY) as? String)?.split(SPACE) ?: emptyList()

    val defaultOptions = listOf(
            JAR_OPTION, directory.resolve("${configuration.executable}$DOT_JAR").toAbsolutePath().toString().wsl(),
            executablePath.absolutePath.wsl(),
            GRAAL_CONFIGURATIONS_PATH_OPTION(configurationPath.toAbsolutePath().toString().wsl())
    )

    val systemProperties = native
            .graalSystemProperties
            .map { entry -> SYSTEM_PROPERTY(entry.key, entry.value) } + SYSTEM_PROPERTY(GRAAL_WORKING_PATH_PROPERTY, directory.toFile().absolutePath.wsl())
    val options = (defaultOptions + native.graalOptions + systemProperties + optionsByProperty).toMutableList()

    val args = native.graalOptionsReplacer(options)

    commandLine(*bashCommand(paths.nativeImage.absolutePath.wsl(), *args.toTypedArray()))
}
