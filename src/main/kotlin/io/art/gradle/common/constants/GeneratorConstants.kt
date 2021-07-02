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

package io.art.gradle.common.constants

import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration
import java.time.Duration.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern

const val GENERATOR = "generator"

const val GENERATOR_CONTROLLER = "generator.controller"

val DEFAULT_WATCHER_PERIOD: Duration = ofMillis(500)
val GENERATOR_DOWNLOAD_TIMEOUT: Duration = ofMinutes(5)
val GENERATOR_STOP_TIMEOUT: Duration = ofSeconds(30)

enum class GeneratorLanguage(val extension: String) {
    JAVA("java"),
    KOTLIN("kt"),
    DART("dart")
}

enum class GeneratorState {
    LOCKED,
    STOPPING,
    AVAILABLE
}

const val WRITE_CONFIGURATION_TASK = "write-generator-configuration"
const val STOP_GENERATOR_TASK = "stop-generator"
const val CLEAN_GENERATOR_TASK = "clean-generator"
const val START_GENERATOR_TASK = "start-generator"
const val GENERATOR_MESSAGE = "Generator is not running. Please, call gradle ':start-generator' task"

const val DISABLE_GENERATOR_PROPERTY = "art.generator.disabled"

val JVM_GENERATOR_DOWNLOAD_URL = { url: String, version: String -> URL("$url/io/art/generator/art-generator/$version/art-generator-$version.jar") }
val JVM_GENERATOR_FILE = { version: String -> Paths.get("art-generator-$version.jar") }
val JVM_GENERATOR_CONFIGURATION_ARGUMENT = { path: Path -> "-Dconfiguration=${path.toFile().absolutePath}" }

val GENERATOR_JVM_OPTIONS = arrayOf(
        "-server",
        "-Xms1g",
        "-Xmx1g",
        "-XX:MetaspaceSize=1g",
        "-XX:MaxMetaspaceSize=1g",
        "-Dfile.encoding=UTF-8"
)
var GENERATOR_DATE_TIME_FORMATTER: DateTimeFormatter = ofPattern("dd-MM-yyyy HH:mm:ss.SSS")
