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

import io.art.gradle.common.configuration.TestConfiguration
import io.art.gradle.common.constants.*
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.kotlin.dsl.getPlugin

fun Project.configureTest(testConfiguration: TestConfiguration) {
    if (testConfiguration.jarEnabled || testConfiguration.nativeEnabled) {
        val creation = JarExecutableCreationConfiguration(
                configuration = testConfiguration.jar,
                runTask = RUN_JAR_TEST_TASK,
                buildTask = BUILD_JAR_TEST_TASK,
                dependencyConfiguration = TEST_EMBEDDED_CONFIGURATION_NAME,
                mainClass = testConfiguration.launcherClass,
                directory = testConfiguration.directory,
                executable = testConfiguration.executableName,
                configurator = {
                    val testSources = project.convention.getPlugin<JavaPluginConvention>().sourceSets.named(TEST).get()
                    dependsOn(testSources.classesTaskName)
                    from(testSources.output)
                }
        )
        configureJar(creation)
    }
    if (testConfiguration.nativeEnabled) {
        val creation = NativeExecutableCreationConfiguration(
                configuration = testConfiguration.native,
                runTask = RUN_NATIVE_TEST_TASK,
                buildTask = BUILD_NATIVE_TEST_TASK,
                buildJarTask = BUILD_JAR_TEST_TASK,
                runAgentTask = RUN_NATIVE_TEST_AGENT,
                mainClass = testConfiguration.launcherClass,
                executable = testConfiguration.executableName,
                directory = testConfiguration.directory
        )
        configureNative(creation)
    }
}

fun Project.addTestEmbeddedConfiguration() {
    configurations.create(TEST_EMBEDDED_CONFIGURATION_NAME)
}

fun Project.configureTestEmbeddedConfiguration() {
    val embedded = configurations.getByName(EMBEDDED_CONFIGURATION_NAME)

    val testEmbedded = configurations.getByName(TEST_EMBEDDED_CONFIGURATION_NAME)
    testEmbedded.extendsFrom(embedded)

    val testImplementation = configurations.findByName(TEST_IMPLEMENTATION_CONFIGURATION_NAME)
    testImplementation?.extendsFrom(testEmbedded)
}
