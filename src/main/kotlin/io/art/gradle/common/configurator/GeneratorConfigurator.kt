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
import io.art.gradle.common.configuration.GeneratorMainConfiguration
import io.art.gradle.common.configuration.GeneratorSourceConfiguration
import io.art.gradle.common.configuration.SourceSet
import io.art.gradle.common.constants.*
import io.art.gradle.common.constants.GeneratorLanguage.JAVA
import io.art.gradle.common.constants.GeneratorLanguage.KOTLIN
import io.art.gradle.common.detector.hasJavaPlugin
import io.art.gradle.common.detector.hasKotlinPlugin
import io.art.gradle.common.generator.GeneratorDownloader.downloadJvmGenerator
import io.art.gradle.common.local.booleanProperty
import io.art.gradle.external.configuration.ExternalConfiguration
import io.art.gradle.internal.configuration.InternalGeneratorConfiguration
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.internal.os.OperatingSystem
import org.gradle.kotlin.dsl.findByType
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.nio.file.Path


fun Project.configureGenerator(configuration: GeneratorConfiguration) {
    if (rootProject != this) return

    if (booleanProperty(DISABLED_GENERATOR_PROPERTY)) {
        return
    }

    val generatorAvailable = allprojects.any { project ->
        project.findGeneratorSourceConfigurations()
                ?.any { configuration -> configuration.value.forDart || configuration.value.forJvm } == true
    }

    if (!generatorAvailable) return

    writeGeneratorConfiguration(configuration.mainConfiguration)

    tasks.register(WRITE_CONFIGURATION_TASK) {
        group = ART
        doLast { writeGeneratorConfiguration(configuration.mainConfiguration) }
    }

    tasks.register(CLEAN_GENERATOR_TASK) {
        group = ART
        doLast { configuration.mainConfiguration.workingDirectory.toFile().deleteRecursively() }
    }

    if (!configuration.mainConfiguration.disabledRunning) {
        val runGenerator = tasks.register(RUN_GENERATOR_TASK) {
            group = ART
            doLast { runGenerator(configuration.mainConfiguration) }
        }

        if (configuration.mainConfiguration.automaticRunning || booleanProperty(ENABLED_AUTO_GENERATOR_PROPERTY)) {
            configureRunDependencies(runGenerator)
        }
    }

    tasks.withType(Delete::class.java) {
        delete = emptySet()
        delete.add(buildDir.listFiles()!!.filter { directory -> directory != configuration.mainConfiguration.workingDirectory.toFile() })
    }
}

private fun Project.configureRunDependencies(runGenerator: TaskProvider<Task>) {
    allprojects.forEach { project ->
        if (project.hasJavaPlugin) {
            project.tasks.withType(JavaCompile::class.java).forEach { task ->
                task.dependsOn(runGenerator)
            }
        }

        if (project.hasKotlinPlugin) {
            project.tasks.filter { task -> task.javaClass.name == KOTLIN_COMPILE_TASK_CLASS }.forEach { task ->
                task.dependsOn(runGenerator)
            }
        }
    }
}

private fun Project.findGeneratorSourceConfigurations(): Map<String, GeneratorSourceConfiguration>? {
    val internal = extensions.findByType<InternalGeneratorConfiguration>()?.sourceConfigurations?.asMap
    val external = extensions.findByType<ExternalConfiguration>()?.generator?.sourceConfigurations?.asMap
    return internal ?: external
}

private fun Project.runGenerator(configuration: GeneratorMainConfiguration) {
    val forJvm = allprojects
            .any { project ->
                project
                        .findGeneratorSourceConfigurations()
                        ?.values
                        ?.any { configuration -> configuration.forJvm } == true
            }
    if (forJvm) {
        configuration.localJarOverridingPath
                ?.let { generatorJar -> runLocalGeneratorJar(configuration, generatorJar) }
                ?: runRemoteGeneratorJar(configuration)
    }
}

private fun Project.runRemoteGeneratorJar(configuration: GeneratorMainConfiguration) {
    val generatorJar = configuration.workingDirectory.resolve(JVM_GENERATOR_FILE(configuration.version))
    if (!generatorJar.toFile().exists()) {
        downloadJvmGenerator(configuration)
    }
    runLocalGeneratorJar(configuration, generatorJar)
}

private fun Project.runLocalGeneratorJar(configuration: GeneratorMainConfiguration, generatorJar: Path) {
    writeGeneratorConfiguration(configuration)
    javaexec {
        executable = configuration.jvmExecutable.toFile().absolutePath
        workingDir(configuration.workingDirectory)
        jvmArgs(*GENERATOR_JVM_OPTIONS)
        jvmArgs(JVM_GENERATOR_CONFIGURATION_ARGUMENT(configuration.workingDirectory.resolve(MODULE_YML)))
        classpath = files(generatorJar.toFile())
        mainClass.set(GENERATOR_MAIN)
    }
}

private fun Project.writeGeneratorConfiguration(configuration: GeneratorMainConfiguration) {
    configuration.workingDirectory.apply { if (!toFile().exists()) toFile().mkdirs() }

    val controllerFile = configuration.workingDirectory.resolve(GENERATOR_CONTROLLER)

    val fileWriter = mapOf(
            "type" to "file",
            "directory" to configuration.loggingDirectory.toFile().absolutePath
    )

    val consoleWriter = mapOf("type" to "console")

    val jvmSources = mutableListOf<SourceSet>()
    allprojects.forEach { project ->
        val generatorConfigurations = project.findGeneratorSourceConfigurations() ?: return@forEach
        for (generatorConfiguration in generatorConfigurations.values) {
            if (generatorConfiguration.forJvm) {
                jvmSources.addAll(project.collectJvmSources(generatorConfiguration))
            }
        }
    }
    val dartSources = emptyList<SourceSet>()
    val allSources = jvmSources + dartSources

    val configurationContent = mapOf(
            "controller" to controllerFile.toFile().absolutePath,
            "logging" to mapOf(
                    "default" to mapOf(
                            "writers" to when {
                                configuration.loggingToConsole -> listOf(consoleWriter)
                                configuration.loggingToDirectory -> listOf(fileWriter)
                                else -> emptyList()
                            }
                    )
            ),
            "sources" to allSources.map { source ->
                mapOf(
                        "languages" to source.languages.map { language -> language.name },
                        "root" to source.root,
                        "classpath" to source.classpath,
                        "module" to source.module,
                        "package" to source.`package`,
                        "sources" to source.sources,
                        "exclusions" to source.classesExclusions.toMutableList(),
                        "inclusions" to source.classesInclusions.toMutableList()
                )
            },
    )

    configuration.workingDirectory
            .resolve(MODULE_YML)
            .toFile()
            .writeText(Yaml().dump(configurationContent))
}

private fun Project.collectJvmSources(configuration: GeneratorSourceConfiguration): Set<SourceSet> {
    val sources = mutableSetOf<SourceSet>()
    val availableFiles = fileTree(projectDir).matching { configuration.sourcesPattern(this) }.files
    extensions.findByType<JavaPluginExtension>()?.apply {
        val compilingSources = sourceSets.flatMap { set -> set.allSource.sourceDirectories }.let { files ->
            if (OperatingSystem.current().isWindows) return@let files.joinToString(SEMICOLON)
            return@let files.joinToString(COLON)
        }
        sourceSets.forEach { set ->
            set.allSource.sourceDirectories
                    .asSequence()
                    .filter { directory -> availableFiles.any { file -> file.startsWith(directory) } }
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
                                classpath = collectClasspath(),
                                module = configuration.module,
                                `package` = configuration.`package`,
                                sources = compilingSources,
                                classesExclusions = configuration.classesExclusions,
                                classesInclusions = configuration.classesInclusions,
                        ))
                    }
        }
    }
    return sources
}

private fun Project.collectClasspath(): String {
    val classpath = mutableSetOf<File>()
    classpath += configurations
            .filter { configuration -> configuration.name.endsWith(COMPILE_CLASS_PATH_CONFIGURATION_NAME, ignoreCase = true) }
            .flatMap { configuration -> configuration.files }
    if (OperatingSystem.current().isWindows) {
        return classpath.joinToString(SEMICOLON)
    }
    return classpath.joinToString(COLON)
}
