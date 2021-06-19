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
import io.art.gradle.external.configuration.ExternalConfiguration
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.internal.os.OperatingSystem
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getPlugin
import org.yaml.snakeyaml.Yaml


fun Project.configureGenerator(configuration: GeneratorConfiguration) {
    if (rootProject != this) return

    if (configuration.forJvm) {
        if (!configuration.workingDirectory.toFile().exists()) {
            configuration.workingDirectory.toFile().mkdirs()
        }

        configuration.localJarOverridingPath?.let { generatorJar ->
            writeJvmGeneratorConfiguration(configuration)
//            javaexec {
//                main = MAIN_CLASS
//                workingDir(configuration.workingDirectory)
//                classpath(generatorJar)
//                jvmArgs(JVM_GENERATOR_CONFIGURATION_ARGUMENT(configuration.workingDirectory.resolve(MODULE_YML)))
//            }
        } ?: let {
            val generatorJar = configuration.workingDirectory.resolve(JVM_GENERATOR_FILE(ART_GENERATOR_NAME, configuration.version))
            if (!generatorJar.toFile().exists()) {
                downloadJvmGenerator(configuration)
            }
//            javaexec {
//                main = MAIN_CLASS
//                workingDir(configuration.workingDirectory)
//                classpath(generatorJar)
//                jvmArgs(JVM_GENERATOR_CONFIGURATION_ARGUMENT(configuration.workingDirectory.resolve(MODULE_YML)))
//            }
        }
    }
    tasks.register(WRITE_CONFIGURATION_TASK) {
        group = ART
        doFirst { writeJvmGeneratorConfiguration(configuration) }
    }
}

private fun Project.writeJvmGeneratorConfiguration(configuration: GeneratorConfiguration) {
    val generatorLock = configuration.workingDirectory.resolve("$GENERATOR$DOT_LOCK")
    configuration.workingDirectory.parent.toFile().mkdirs()

    val fileWriter = mapOf(
            "type" to "file",
            "directory" to configuration.loggingDirectory.toFile().absolutePath
    )
    val consoleWriter = mapOf(
            "type" to "console",
            "colored" to true
    )

    val contentMap = mapOf(
            "lock" to generatorLock.toFile().absolutePath,
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
            "sources" to allprojects
                    .filter { project ->
                        val extensions = project.extensions
                        extensions.findByType<GeneratorConfiguration>() != null || extensions.findByType<ExternalConfiguration>()?.generator != null
                    }
                    .flatMap { project ->
                        project
                                .collectSources()
                                .map { source ->
                                    mapOf(
                                            "languages" to source.languages.map { language -> language.name },
                                            "root" to source.root,
                                            "classpath" to source.classpath,
                                            "module" to source.module
                                    )
                                }
                    },
    )

    configuration.workingDirectory.resolve(MODULE_YML).toFile().writeText(Yaml().dump(contentMap))
}

private fun Project.collectSources(): Set<SourceSet> {
    val extensions = project.extensions
    val configuration = extensions.findByType() ?: extensions.findByType<ExternalConfiguration>()!!.generator
    val sources = mutableSetOf<SourceSet>()
    project.convention.getPlugin<JavaPluginConvention>().sourceSets.forEach { set ->
        set.allSource.sourceDirectories.forEach { directory ->
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
    val classpath = configurations.getByName(EMBEDDED_CONFIGURATION_NAME)
    if (OperatingSystem.current().isWindows) {
        return classpath.files.joinToString(SEMICOLON)
    }
    return classpath.files.joinToString(COLON)
}
