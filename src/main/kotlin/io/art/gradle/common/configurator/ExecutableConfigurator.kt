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
    if (executableConfiguration.jarEnabled) {
        val configuration = JarExecutableCreationConfiguration(
                configuration = executableConfiguration.jar,
                runTask = RUN_JAR_EXECUTABLE_TASK,
                buildTask = BUILD_JAR_EXECUTABLE_TASK,
                dependencyConfiguration = EMBEDDED_CONFIGURATION_NAME,
                mainClass = executableConfiguration.mainClass,
                directory = executableConfiguration.directory,
                executable = executableConfiguration.executableName
        )
        configureJar(configuration)
    }
    if (executableConfiguration.packagingEnabled) {
        val configuration = PackagingCreationConfiguration(
            configuration = executableConfiguration.packaging,
            buildTask = PACKAGE_TASK,
            dependencyConfiguration = EMBEDDED_CONFIGURATION_NAME,
            mainClass = executableConfiguration.mainClass,
            directory = executableConfiguration.directory,
            executable = executableConfiguration.executableName
        )
        configurePackaging(configuration)
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
