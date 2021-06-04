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
import io.art.gradle.common.constants.COLON
import io.art.gradle.common.constants.COMPILE_CLASS_PATH_CONFIGURATION_NAME
import io.art.gradle.common.constants.SEMICOLON
import io.art.gradle.common.constants.WRITE_CONFIGURATION_TASK
import org.gradle.api.Project
import org.gradle.internal.os.OperatingSystem
import org.yaml.snakeyaml.Yaml


fun Project.configureGenerator(configuration: GeneratorConfiguration) {
    tasks.register(WRITE_CONFIGURATION_TASK) {
        doLast {
            writeConfiguration(configuration)
        }
    }
}

fun Project.writeConfiguration(configuration: GeneratorConfiguration) {
    configuration.configurationPath.parent.toFile().mkdirs()

    val contentMap = mapOf(
            "logging" to mapOf(
                    "default" to mapOf(
                            "writers" to listOf(
                                    mapOf(
                                            "type" to "file",
                                            "directory" to configuration.loggingDirectory.toFile().absolutePath
                                    )
                            )
                    )
            ),
            "watcher" to mapOf("period" to configuration.watcherPeriod.toMillis()),
            "classpath" to collectClasspath(),
            "paths" to mapOf("sources" to configuration.sourceSets)
    )

    configuration.configurationPath.toFile().writeText(Yaml().dump(contentMap))
}

private fun Project.collectClasspath(): String {
    val compileClasspath = configurations.getByName(COMPILE_CLASS_PATH_CONFIGURATION_NAME)
    if (OperatingSystem.current().isWindows) {
        return compileClasspath.files.joinToString(SEMICOLON)
    }
    return compileClasspath.files.joinToString(COLON)
}
