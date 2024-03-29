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

package io.art.gradle.external.constants

const val JAVA_GROUP = "io.art.java"
const val KOTLIN_GROUP = "io.art.kotlin"

enum class ArtVersion(val version: String) {
    MAIN("main")
}

enum class JavaModules(val artifact: String) {
    CORE("core"),
    LOGGING("logging"),
    SCHEDULER("scheduler"),
    CONFIGURATOR("configurator"),
    JSON("json"),
    MESSAGE_PACK("message-pack"),
    YAML("yaml"),
    TRANSPORT("transport"),
    HTTP("http"),
    RSOCKET("rsocket"),
    TARANTOOL("tarantool"),
    META("meta"),
    LAUNCHER("launcher"),
    TESTS("tests")
}

enum class KotlinModules(val artifact: String) {
    CORE("core"),
    LOGGING("logging"),
    SCHEDULER("scheduler"),
    CONFIGURATOR("configurator"),
    JSON("json"),
    MESSAGE_PACK("message-pack"),
    YAML("yaml"),
    RSOCKET("rsocket"),
    HTTP("http"),
    META("meta"),
    TRANSPORT("transport"),
    LAUNCHER("launcher"),
    TESTS("tests")
}

