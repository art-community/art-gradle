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
import org.gradle.api.*
import org.gradle.api.plugins.*
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.DependencyConfiguration.*
import ru.art.gradle.constants.DependencyConfiguration.GATLING
import ru.art.gradle.logging.*
import ru.art.gradle.logging.LogMessageColor.*
import ru.art.gradle.provider.*

fun Project.configureGatling() {
    configure<GatlingPluginExtension> {
        simulations = fileTree(SIMULATIONS_PATH).files.map { "$SIMULATIONS_PREFIX.${it.name.removeSuffix(SCALA_POSTFIX)}" }
    }

    convention.getPlugin(JavaPluginConvention::class.java).sourceSets {
        GATLING {
            withConvention(ScalaSourceSet::class) {
                scala { source ->
                    source.setSrcDirs(source.srcDirs.apply { add(file(GATLING_SOURCE_SET_DIR)) }
                            .filter { directory -> !directory.absolutePath.endsWith(SIMULATIONS_PATH) })
                }
            }
        }
    }

    addDependency(GATLING, logbackClassic())
    addDependency(GATLING, scala())
    addDependency(PROVIDED, scala())
    addDependency(PROVIDED, gatlingHttp())
    addDependency(PROVIDED, gatlingCore())

    with(configurations) {
        getByName(GATLING.configuration).extendsFrom(getByName(PROVIDED.configuration),
                getByName(EMBEDDED.configuration),
                getByName(TEST_COMPILE_CLASSPATH.configuration),
                getByName(TEST_RUNTIME_CLASSPATH.configuration))
        getByName(GATLING_COMPILE.configuration).extendsFrom(getByName(PROVIDED.configuration),
                getByName(EMBEDDED.configuration),
                getByName(TEST_COMPILE_CLASSPATH.configuration))
        getByName(GATLING_RUNTIME.configuration).extendsFrom(getByName(PROVIDED.configuration),
                getByName(EMBEDDED.configuration),
                getByName(TEST_RUNTIME_CLASSPATH.configuration))
    }

    gatlingRunTask().dependsOn(buildTask())

    success("Configuring Gatling:\n" + message("""
        'gatling' dependency configuration extends from embedded, provided, testCompileClasspath and testRuntimeClasspath
        'gatlingCompile' dependency configuration extends from embedded, provided, testCompileClasspath
        'gatlingRuntime' dependency configuration extends from embedded, provided, testRuntimeClasspath
        Simulations = ${fileTree(SIMULATIONS_DIR).files.map { "$SIMULATIONS_PREFIX.${it.name.removeSuffix(SCALA_POSTFIX)}" }}
        Sources directory = $GATLING_SOURCE_SET_DIR
        (!) gatlingRun depends on build
        """.replaceIndent(ADDITIONAL_LOGGING_MESSAGE_INDENT), PURPLE_BOLD))
}
