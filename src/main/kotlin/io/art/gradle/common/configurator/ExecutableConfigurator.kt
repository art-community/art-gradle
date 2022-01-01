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

import io.art.gradle.common.configuration.ExecutableConfiguration
import io.art.gradle.common.constants.*
import org.gradle.api.Project

fun Project.configureExecutable(executableConfiguration: ExecutableConfiguration) {
    if (executableConfiguration.jarEnabled || executableConfiguration.nativeEnabled) {
        val creation = JarExecutableCreationConfiguration(
                configuration = executableConfiguration.jar,
                runTask = RUN_JAR_EXECUTABLE_TASK,
                buildTask = BUILD_JAR_EXECUTABLE_TASK,
                dependencyConfiguration = EMBEDDED_CONFIGURATION_NAME,
                mainClass = executableConfiguration.mainClass,
                directory = executableConfiguration.directory,
                executable = executableConfiguration.executableName
        )
        configureJar(creation)
    }
    if (executableConfiguration.nativeEnabled) {
        val creation = NativeExecutableCreationConfiguration(
                configuration = executableConfiguration.native,
                runTask = RUN_NATIVE_EXECUTABLE_TASK,
                buildTask = BUILD_NATIVE_EXECUTABLE_TASK,
                buildJarTask = BUILD_JAR_EXECUTABLE_TASK,
                runAgentTask = RUN_NATIVE_AGENT,
                mainClass = executableConfiguration.mainClass,
                executable = executableConfiguration.executableName,
                directory = executableConfiguration.directory
        )
        configureNative(creation)
    }
}

fun Project.addEmbeddedConfiguration() {
    configurations.create(EMBEDDED_CONFIGURATION_NAME)
}

fun Project.configureEmbeddedConfiguration() {
    val embedded = configurations.getByName(EMBEDDED_CONFIGURATION_NAME)
    val implementation = configurations.findByName(IMPLEMENTATION_CONFIGURATION_NAME)
    implementation?.extendsFrom(embedded)
}
