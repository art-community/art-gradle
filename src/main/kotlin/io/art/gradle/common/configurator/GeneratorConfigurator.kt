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
import io.art.gradle.common.constants.GeneratorState.AVAILABLE
import io.art.gradle.common.constants.GeneratorState.STOPPING
import io.art.gradle.common.generator.GeneratorDownloader.downloadJvmGenerator
import io.art.gradle.common.service.JavaForkRequest
import io.art.gradle.common.service.ProcessExecutionService.forkJava
import io.art.gradle.common.service.writeContent
import io.art.gradle.external.configuration.ExternalConfiguration
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.internal.os.OperatingSystem
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getPlugin
import org.yaml.snakeyaml.Yaml
import java.nio.file.Path
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit.MILLISECONDS


fun Project.configureGenerator(configuration: GeneratorConfiguration) {
    if (rootProject != this) return
    configuration.workingDirectory.apply { if (!toFile().exists()) toFile().mkdirs() }

    if (configuration.activateAutomatically) activateGenerator(configuration)

    tasks.register(WRITE_CONFIGURATION_TASK) {
        group = ART
        doLast { writeGeneratorConfiguration(configuration) }
    }

    val stop = tasks.register(STOP_GENERATOR_TASK) {
        group = ART
        doLast { stopGenerator(configuration) }
    }

    tasks.register(RESTART_GENERATOR_TASK) {
        group = ART
        dependsOn(stop)
        doLast { restartGenerator(configuration) }
    }
}

private fun Project.restartGenerator(configuration: GeneratorConfiguration) {
    stopGenerator(configuration)
    activateGenerator(configuration)
}

private fun stopGenerator(configuration: GeneratorConfiguration) {
    val controllerFile = configuration.workingDirectory.resolve(GENERATOR_CONTROLLER)
    controllerFile.writeContent(STOPPING.name)
    val latch = CountDownLatch(1)
    val scheduler = ScheduledThreadPoolExecutor(1).apply { maximumPoolSize = 1 }
    fun check() {
        if (!controllerFile.toFile().exists()) {
            latch.countDown()
            return
        }
        if (GeneratorState.valueOf(controllerFile.toFile().readText().split(SPACE)[0]) == AVAILABLE) latch.countDown()
    }
    scheduler.scheduleAtFixedRate(::check, 0L, GENERATOR_STOP_CHECKING_PERIOD.toMillis(), MILLISECONDS)
    if (!latch.await(GENERATOR_STOP_TIMEOUT.toMillis(), MILLISECONDS)) throw generatorStopTimeoutError()
}

private fun Project.activateGenerator(configuration: GeneratorConfiguration) {
    if (configuration.forJvm) {
        configuration.localJarOverridingPath
                ?.let { generatorJar -> runLocalGeneratorJar(configuration, generatorJar) }
                ?: runRemoteGeneratorJar(configuration)
    }
}

private fun Project.runRemoteGeneratorJar(configuration: GeneratorConfiguration) {
    val generatorJar = configuration.workingDirectory.resolve(JVM_GENERATOR_FILE(configuration.version))
    if (!generatorJar.toFile().exists()) {
        downloadJvmGenerator(configuration)
    }
    runLocalGeneratorJar(configuration, generatorJar)
}

private fun Project.runLocalGeneratorJar(configuration: GeneratorConfiguration, generatorJar: Path) {
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
    val controllerFile = configuration.workingDirectory.resolve(GENERATOR_CONTROLLER)

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
            "controller" to controllerFile.toFile().absolutePath,
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
    val availableFiles = fileTree(project.projectDir).matching { configuration.sourcesPattern(this) }.files
    project.convention.getPlugin<JavaPluginConvention>().sourceSets.forEach { set ->
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
