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

val DEFAULT_WATCHER_PERIOD: Duration = Duration.ofMillis(500)
const val MAIN_CLASS = "io.art.generator.Generator"
const val GENERATOR = "generator"
const val ART_GENERATOR_NAME = "art-generator"
val GENERATOR_LOCK_TIMEOUT: Duration = Duration.ofMinutes(1)

enum class GeneratorLanguage {
    JAVA,
    KOTLIN,
    DART
}

const val WRITE_CONFIGURATION_TASK = "write-generator-configuration"

val JVM_GENERATOR_DOWNLOAD_URL = { url: String, name: String, version: String -> URL("$url/$name/$version/$name-$version.jar") }
val JVM_GENERATOR_FILE = { name: String, version: String -> Paths.get("$name-$version.jar") }

val JVM_GENERATOR_CONFIGURATION_ARGUMENT = { path: Path -> "-Dconfiguration=${path.toFile().absolutePath}" }
