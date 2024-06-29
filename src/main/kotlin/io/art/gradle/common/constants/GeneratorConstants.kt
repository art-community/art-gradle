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

package io.art.gradle.common.constants

import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration
import java.time.Duration.ofMinutes

const val GENERATOR = "generator"

const val GENERATOR_CONTROLLER = "generator.controller"

val GENERATOR_DOWNLOAD_TIMEOUT: Duration = ofMinutes(5)

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
const val RUN_GENERATOR_TASK = "run-generator"
const val CLEAN_GENERATOR_TASK = "clean-generator"

const val DISABLED_GENERATOR_PROPERTY = "art.generator.disabled"
const val ENABLED_AUTO_GENERATOR_PROPERTY = "art.generator.auto.enabled"

val JVM_GENERATOR_DOWNLOAD_URL = { url: String, version: String -> URI("$url/io/art/generator/art-generator/$version/art-generator-$version.jar") }
val JVM_GENERATOR_FILE = { version: String -> Paths.get("art-generator-$version.jar") }
val JVM_GENERATOR_CONFIGURATION_ARGUMENT = { path: Path -> "-Dconfiguration=${path.toFile().absolutePath}" }

val GENERATOR_JVM_OPTIONS = arrayOf(
    "-Xms1g", "-Dfile.encoding=UTF-8",
    "--add-exports", "jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
    "--add-exports", "jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
    "--add-exports", "jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
    "--add-exports", "jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
    "--add-exports", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
    "--add-exports", "jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
    "--add-exports", "jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
    "--add-exports", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
)
const val GENERATOR_MAIN = "io.art.generator.Generator"

const val LANGUAGES_KEY = "languages"
const val INCLUSIONS_KEY = "inclusions"
const val EXCLUSIONS_KEY = "exclusions"
const val CLASSPATH_KEY = "classpath"
const val SOURCES_KEY = "sources"
const val ROOT_KEY = "root"
const val MODULE_KEY = "module"
const val PACKAGE_KEY = "package"
const val CONTROLLER_KEY = "controller"
const val TYPE_KEY = "type"
const val CONSOLE_KEY = "console"
const val FILE_KEY = "file"
const val DIRECTORY_KEY = "directory"
const val LOGGING_KEY = "logging"
const val DEFAULT_KEY = "default"
const val WRITERS_KEY = "writers"
