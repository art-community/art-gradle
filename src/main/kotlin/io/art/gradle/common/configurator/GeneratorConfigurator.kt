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
import io.art.gradle.common.constants.*
import org.gradle.api.Project
import org.gradle.internal.os.OperatingSystem
import org.yaml.snakeyaml.Yaml


fun Project.configureGenerator(configuration: GeneratorConfiguration) {
    tasks.register(WRITE_CONFIGURATION_TASK) {
        group = ART
        doFirst { writeConfiguration(configuration) }
    }
}

private fun Project.writeConfiguration(configuration: GeneratorConfiguration) {
    configuration.configurationPath.parent.toFile().mkdirs()

    val fileWriter = mapOf(
            "type" to "file",
            "directory" to configuration.loggingDirectory.toFile().absolutePath
    )
    val consoleWriter = mapOf(
            "type" to "console",
            "colored" to true
    )

    val contentMap = mapOf(
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
            "classpath" to collectClasspath(),
            "sources" to configuration.sourceSets.values.map { set ->
                mapOf(
                        "languages" to set.languages.map { language -> language.name },
                        "path" to set.root.toFile().absolutePath,
                        "module" to configuration.module
                )
            },
    )

    configuration.configurationPath.toFile().writeText(Yaml().dump(contentMap))
}

private fun Project.collectClasspath(): String {
    val classpath = configurations.getByName(EMBEDDED_CONFIGURATION_NAME)
    if (OperatingSystem.current().isWindows) {
        return classpath.files.joinToString(SEMICOLON)
    }
    return classpath.files.joinToString(COLON)
}
