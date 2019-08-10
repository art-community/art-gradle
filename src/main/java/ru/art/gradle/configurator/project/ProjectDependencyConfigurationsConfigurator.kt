/*
 * Copyright 2019 ART
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.art.gradle.configurator.project

import org.gradle.api.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.DependencyConfiguration.*
import ru.art.gradle.logging.*
import ru.art.gradle.logging.LogMessageColor.*

fun Project.addDependencyConfigurations(): Unit = configurations.run {
    create(EMBEDDED.configuration)
    create(PROVIDED.configuration)

    getByName(COMPILE_CLASSPATH.configuration).extendsFrom(getByName(EMBEDDED.configuration), getByName(PROVIDED.configuration))
    getByName(API.configuration).extendsFrom(getByName(EMBEDDED.configuration))
    getByName(TEST_COMPILE_CLASSPATH.configuration).extendsFrom(getByName(EMBEDDED.configuration), getByName(PROVIDED.configuration))
    getByName(TEST_RUNTIME_CLASSPATH.configuration).extendsFrom(getByName(EMBEDDED.configuration), getByName(PROVIDED.configuration))

    success("Created two ART gradle configurations ${message("(embedded and provided)", CYAN_BOLD)}:$NEW_LINE" + message("""
            (*) compileClasspath extends from embedded
            (*) api extends from embedded
            (*) testCompileClasspath extends from embedded
            (*) compileClasspath extends from provided
            (*) api extends from provided
            (*) testCompileClasspath extends from provided
            """.replaceIndent(ADDITIONAL_LOGGING_MESSAGE_INDENT), PURPLE_BOLD) + NEW_LINE +
            message("""(!) embedded dependency artifacts will be included inside .jar""".replaceIndent(ADDITIONAL_LOGGING_MESSAGE_INDENT), GREEN_BOLD))
}