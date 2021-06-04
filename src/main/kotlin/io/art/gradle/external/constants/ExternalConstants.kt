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

package io.art.gradle.external.constants

const val JAVA_GROUP = "io.art.java"
const val KOTLIN_GROUP = "io.art.kotlin"

enum class ArtVersion(val version: String) {
    MAIN("main")
}

enum class JavaModules(val artifact: String) {
    CORE("core"),
    LOGGING("logging"),
    VALUE("value"),
    SCHEDULER("scheduler"),
    CONFIGURATOR("configurator"),
    JSON("json"),
    MESSAGE_PACK("message-pack"),
    YAML_CONFIGURATION("yaml-configuration"),
    YAML("yaml"),
    RESILIENCE("resilience"),
    TRANSPORT("transport"),
    SERVER("server"),
    COMMUNICATOR("communicator"),
    HTTP("http"),
    RSOCKET("rsocket"),
    TARANTOOL("tarantool"),
    META("meta"),
    GRAAL("graal"),
    STORAGE("storage"),
    MODEL("model"),
    LAUNCHER("launcher")
}

enum class KotlinModules(val artifact: String) {
}

const val KOTLIN_JVM_PLUGIN_ID = "org.jetbrains.kotlin.jvm"
