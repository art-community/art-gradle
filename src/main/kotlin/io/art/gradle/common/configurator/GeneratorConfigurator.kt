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

package io.art.gradle.common.configurator

import io.art.gradle.common.configuration.GeneratorConfiguration
import io.art.gradle.common.configuration.SourceSet
import io.art.gradle.common.constants.*
import io.art.gradle.common.constants.GeneratorLanguage.JAVA
import io.art.gradle.common.constants.GeneratorLanguage.KOTLIN
import io.art.gradle.common.generator.GeneratorDownloader.downloadJvmGenerator
import io.art.gradle.common.service.JavaForkRequest
import io.art.gradle.common.service.ProcessExecutionService.forkJava
import io.art.gradle.external.configuration.ExternalConfiguration
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.internal.os.OperatingSystem
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getPlugin
import org.yaml.snakeyaml.Yaml
import java.nio.file.Path


fun Project.configureGenerator(configuration: GeneratorConfiguration) {
    if (rootProject != this) return

    activateGenerator(configuration)

    tasks.register(WRITE_CONFIGURATION_TASK) {
        group = ART
        doLast { writeGeneratorConfiguration(configuration) }
    }

    tasks.register(DELETE_GENERATOR_LOCK_TASK) {
        group = ART
        doLast {
            configuration.workingDirectory
                    .resolve("$GENERATOR$DOT_LOCK")
                    .toFile()
                    .delete()
        }
    }

    val stop = tasks.register(STOP_GENERATOR_TASK) {
        group = ART
        doLast {
            val stopFile = configuration.workingDirectory.resolve("$GENERATOR$DOT_STOP")
            stopFile.toFile().createNewFile()
        }
    }

    tasks.register(RESTART_GENERATOR_TASK) {
        group = ART
        dependsOn(stop)
        doLast { activateGenerator(configuration) }
    }
}

private fun Project.activateGenerator(configuration: GeneratorConfiguration) {
    val workingDirectory = configuration.workingDirectory
    if (configuration.forJvm) {
        val generatorLock = configuration.workingDirectory.resolve("$GENERATOR$DOT_LOCK")
        if (generatorLock.toFile().exists()) {
            return
        }
        if (!workingDirectory.toFile().exists()) {
            workingDirectory.toFile().mkdirs()
        }
        configuration.localJarOverridingPath
                ?.let { generatorJar -> if (configuration.autoRun) runJvmGenerator(configuration, generatorJar) }
                ?: let {
                    val generatorJar = workingDirectory.resolve(JVM_GENERATOR_FILE(configuration.version))
                    if (!generatorJar.toFile().exists()) {
                        downloadJvmGenerator(configuration)
                    }
                    if (configuration.autoRun) runJvmGenerator(configuration, generatorJar)
                }
    }
}

private fun Project.runJvmGenerator(configuration: GeneratorConfiguration, generatorJar: Path) {
    writeGeneratorConfiguration(configuration)
    val request = JavaForkRequest(
            executable = configuration.jvmExecutable,
            jar = generatorJar,
            arguments = listOf(
                    JVM_GENERATOR_CONFIGURATION_ARGUMENT(configuration.workingDirectory.resolve(MODULE_YML)),
                    *GENERATOR_JVM_OPTIONS
            ),
            directory = configuration.workingDirectory
    )
    forkJava(request)
}

private fun Project.writeGeneratorConfiguration(configuration: GeneratorConfiguration) {
    val generatorLock = configuration.workingDirectory.resolve("$GENERATOR$DOT_LOCK")
    val generatorStop = configuration.workingDirectory.resolve("$GENERATOR$DOT_STOP")
    if (!configuration.workingDirectory.toFile().exists()) {
        configuration.workingDirectory.toFile().mkdirs()
    }

    val fileWriter = mapOf(
            "type" to "file",
            "directory" to configuration.loggingDirectory.toFile().absolutePath
    )
    val consoleWriter = mapOf(
            "type" to "console",
            "colored" to true
    )

    val jvmSources = allprojects
            .filter { project ->
                val extensions = project.extensions
                val generatorConfiguration = extensions.findByType<GeneratorConfiguration>()
                val externalConfiguration = extensions.findByType<ExternalConfiguration>()?.generator
                generatorConfiguration?.forJvm == true || externalConfiguration?.forJvm == true
            }
            .flatMap { project -> project.collectJvmSources() }
    val dartSources = emptyList<SourceSet>()
    val allSources = jvmSources + dartSources

    val configurationContent = mapOf(
            "marker" to mapOf(
                    "lock" to generatorLock.toFile().absolutePath,
                    "stop" to generatorStop.toFile().absolutePath
            ),
            "logging" to mapOf(
                    "default" to mapOf(
                            "writers" to listOf(
                                    when {
                                        configuration.loggingToConsole -> consoleWriter
                                        configuration.loggingToDirectory -> fileWriter
                                        else -> emptyMap()
                                    }
                            )
                    )
            ),
            "watcher" to mapOf("period" to configuration.watcherPeriod.toMillis()),
            "sources" to allSources.map { source ->
                mapOf(
                        "languages" to source.languages.map { language -> language.name },
                        "root" to source.root,
                        "classpath" to source.classpath,
                        "module" to source.module
                )
            },
    )

    configuration.workingDirectory
            .resolve(MODULE_YML)
            .toFile()
            .writeText(Yaml().dump(configurationContent))
}

private fun Project.collectJvmSources(): Set<SourceSet> {
    val extensions = project.extensions
    val configuration = extensions.findByType() ?: extensions.findByType<ExternalConfiguration>()!!.generator
    val sources = mutableSetOf<SourceSet>()
    project.convention.getPlugin<JavaPluginConvention>().sourceSets.forEach { set ->
        set.allSource.sourceDirectories
                .asSequence()
                .filter { directory -> directory.name !in configuration.directoryExclusions }
                .forEach { directory ->
                    val hasJava = directory.walkTopDown().any { file -> file.extension == JAVA.extension }
                    val hasKotlin = directory.walkTopDown().any { file -> file.extension == KOTLIN.extension }
                    val languages = mutableSetOf<GeneratorLanguage>()
                    if (hasJava) {
                        languages += JAVA
                    }
                    if (hasKotlin) {
                        languages += KOTLIN
                    }
                    if (languages.isNotEmpty()) sources.add(SourceSet(
                            languages = languages,
                            root = directory.absolutePath,
                            classpath = project.collectClasspath(),
                            module = configuration.module
                    ))
                }
    }
    return sources
}

private fun Project.collectClasspath(): String {
    val classpath = configurations.getByName(COMPILE_CLASS_PATH_CONFIGURATION_NAME)
    if (OperatingSystem.current().isWindows) {
        return classpath.files.joinToString(SEMICOLON)
    }
    return classpath.files.joinToString(COLON)
}
