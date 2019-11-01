/*
 * ART Java
 *
 * Copyright 2019 ART
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

package ru.art.gradle.configurator.project

import org.gradle.api.Project
import org.gradle.api.plugins.*
import org.gradle.api.tasks.*
import org.gradle.jvm.tasks.*
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.ide.idea.model.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.DependencyConfiguration.*
import ru.art.gradle.constants.DependencyConfiguration.GATLING
import ru.art.gradle.constants.IdeaScopeOperations.PLUS
import ru.art.gradle.constants.IdeaScopes.COMPILE
import ru.art.gradle.logging.*
import ru.art.gradle.logging.LogMessageColor.*
import ru.art.gradle.provider.*
import java.io.File.*
import java.util.*

fun Project.configureGatling() {
    addGatlingDependencies()
    configureGatlingSourceSet()
    val simulations = collectGatlingSimulations()
    createRunGatlingTask(simulations)

    success("Configuring Gatling:\n" + message("""
        Sources directory = $GATLING_SOURCE_SET_DIR
        Creating 'gatling' configuration which extends 'provided', 'embedded', 'testCompileClasspath' and 'testRuntimeClasspath'
        Gatling simulations = $simulations
        (!) all gatling running tasks depends on createGatlingLauncher task that depends on build task
        """.replaceIndent(ADDITIONAL_LOGGING_MESSAGE_INDENT), PURPLE_BOLD))
}

private fun Project.createRunGatlingTask(simulations: Set<String>) {
    val createGatling = tasks.register<Jar>(CREATE_GATLING_LAUNCHER_TASK) {
        group = GATLING_GROUP
        archiveBaseName.set(GATLING_LAUNCHER)
        dependsOn += compileGatlingScala()
        dependsOn += buildTask()
        doFirst {
            manifest { manifest ->
                with(manifest) {
                    attributes[CLASS_PATH_ATTRIBUTE] = configurations[GATLING.configuration].files.joinToString(SPACE) { file -> uri(file).toString() }
                }
            }
        }
    }

    tasks.register<Jar>(RUN_ALL_GATLING_SIMULATIONS_TASK) {
        group = GATLING_GROUP
        dependsOn.add(createGatling)
        doLast {
            simulations.forEach { simulation ->
                try {
                    gatlingExec(simulation, createGatling.get())
                } catch (e: Exception) {
                    error("$e:\n${Arrays.toString(e.stackTrace)}")
                }
            }

        }
    }

    simulations.forEach { simulation ->
        tasks.register<Jar>(RUN_GATLING_SIMULATION_TASK(simulation)) {
            group = GATLING_GROUP
            dependsOn.add(createGatling)
            doLast {
                gatlingExec(simulation, createGatling.get())
            }
        }

    }
}

private fun Project.gatlingExec(simulation: String, createGatling: Jar) = javaexec { exec ->
    with(exec) {
        val javaPlugin = convention.getPlugin(JavaPluginConvention::class.java)
        val gatlingSourceSet = javaPlugin.sourceSets[GATLING.configuration]
        val mainSourceSet = javaPlugin.sourceSets[MAIN_SOURCE_SET]
        val testSourceSet = javaPlugin.sourceSets[TEST_SOURCE_SET]
        val scalaBinariesDirectory = gatlingSourceSet.output.classesDirs.filter { file -> file.parentFile.name == SCALA_EXTENSION }.first()
        jvmArgs(GATLING_JVM_ARGS)
        args("-bf", scalaBinariesDirectory,
                "-rsf", gatlingSourceSet.output.resourcesDir,
                "-rf", "${buildDir}$separator$GATLING_REPORTS",
                "-s", simulation)
        main = GATLING_MAIN_CLASS
        classpath = gatlingSourceSet.output + mainSourceSet.output + testSourceSet.output + files(createGatling.archiveFile.get().asFile)
    }
}

private fun Project.configureGatlingSourceSet() = with(convention.getPlugin(JavaPluginConvention::class.java).sourceSets.create(GATLING.configuration)) {
    withConvention(ScalaSourceSet::class) {
        scala { source -> source.srcDir(GATLING_SOURCE_SET_DIR) }
        compileClasspath = configurations[GATLING.configuration]
    }
}

private fun Project.addGatlingDependencies() {
    with(configurations) {
        create(GATLING.configuration).extendsFrom(getByName(COMPILE_CLASSPATH.configuration),
                getByName(EMBEDDED.configuration),
                getByName(PROVIDED.configuration),
                getByName(TEST_COMPILE_CLASSPATH.configuration),
                getByName(TEST_RUNTIME_CLASSPATH.configuration))
    }
    extensions.configure(IdeaModel::class.java) { model ->
        model.module { module ->
            with(module) {
                scopes[COMPILE]?.get(PLUS)?.addAll(listOf(configurations[GATLING.configuration]))
            }
        }
    }
    addDependency(GATLING, logbackClassic())
    addDependency(GATLING, scala())
    addDependency(COMPILE_CLASSPATH, scala())
    addDependency(GATLING, gatlingHttp())
    addDependency(GATLING, gatlingCore())
    addDependency(GATLING, gatlingHighCharts())
}

private fun Project.collectGatlingSimulations(): Set<String> = convention.getPlugin(JavaPluginConvention::class.java)
        .sourceSets[GATLING.configuration]
        .allSource
        .files
        .filter { file -> file.extension == SCALA_EXTENSION }
        .filter { file -> file.readText().contains(SIMULATION_MARKER) }
        .map { file -> file.absolutePath.substringAfter("${GATLING_SOURCE_SET_DIR}${separator}").replace(separator, DOT).replace("$DOT$SCALA_EXTENSION", EMPTY_STRING) }
        .toSet()