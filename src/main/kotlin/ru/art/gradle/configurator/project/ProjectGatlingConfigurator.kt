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

import com.github.lkishalmi.gradle.gatling.*
import org.gradle.api.Project
import org.gradle.api.plugins.*
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.ide.idea.model.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.DependencyConfiguration.*
import ru.art.gradle.constants.DependencyConfiguration.GATLING
import ru.art.gradle.constants.IdeaScopeOperations.PLUS
import ru.art.gradle.constants.IdeaScopes.COMPILE
import ru.art.gradle.context.Context.projectExtension
import ru.art.gradle.logging.*
import ru.art.gradle.logging.LogMessageColor.*
import ru.art.gradle.provider.*
import java.io.*
import java.io.File.*

fun Project.configureGatling() {
    val resolvedSimulations = fileTree(SIMULATIONS_PATH)
            .files
            .filter(File::isFile)
            .filter { file -> file.readText().contains(SIMULATION_MARKER) }
            .map { file -> "$SIMULATIONS_PACKAGE.${file.name.substringAfter(SIMULATIONS_PACKAGE).replace(separator, DOT).removeSuffix(SCALA_SUFFIX)}" }
    configure<GatlingPluginExtension> {
        simulations = resolvedSimulations
    }

    convention.getPlugin(JavaPluginConvention::class.java).sourceSets {
        GATLING {
            withConvention(ScalaSourceSet::class) {
                scala { source ->
                    source.setSrcDirs(source.srcDirs.filter { directory -> directory.path != file(SIMULATIONS_PATH).absolutePath }.toMutableList().apply { add(file(GATLING_SOURCE_SET_DIR)) })
                }
            }
        }
    }

    addDependency(GATLING, logbackClassic())
    addDependency(GATLING, scala())
    addDependency(COMPILE_CLASSPATH, scala())
    addDependency(RUNTIME_CLASSPATH, scala())
    addDependency(GATLING, gatlingHttp())
    addDependency(GATLING, gatlingCore())

    projectExtension().gatlingConfiguration.modulesConfiguration.modules.stream()
            .peek(::substituteModuleWithCode)
            .peek { dependency -> setVersion(dependency, projectExtension().gatlingConfiguration.modulesConfiguration) }
            .forEach { addDependency(GATLING, it) }


    extensions.configure(IdeaModel::class.java) { model ->
        model.module { module ->
            with(module) {
                scopes[COMPILE]?.get(PLUS)?.addAll(listOf(configurations.getByName(GATLING.configuration)))
            }
        }
    }

    gatlingRunTask().dependsOn(buildTask())

    success("Configuring Gatling:\n" + message("""
        Simulations = $resolvedSimulations"
        Sources directory = $GATLING_SOURCE_SET_DIR
        (!) gatlingRun depends on build
        """.replaceIndent(ADDITIONAL_LOGGING_MESSAGE_INDENT), PURPLE_BOLD))
}
