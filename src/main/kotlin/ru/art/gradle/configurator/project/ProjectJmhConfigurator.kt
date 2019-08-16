/*
 * ART Java
 *
 * Copyright 2019 ART
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

package ru.art.gradle.configurator.project

import me.champeau.gradle.*
import org.gradle.api.*
import org.gradle.api.file.DuplicatesStrategy.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.JmhResultFormat.*
import ru.art.gradle.logging.*
import ru.art.gradle.logging.LogMessageColor.*
import ru.art.gradle.provider.*


fun Project.configureJmh() {
    extensions.configure(JMHPluginExtension::class.java) { jmh ->
        with(jmh) {
            duplicateClassesStrategy = WARN
            resultFormat = JSON.name
        }
    }
    jmhCompileGeneratedClassesTask().dependsOn(buildTask())

    success("Configuring JMH:\n" + message("""
        duplicateClassesStrategy = WARN
        resultFormat = JSON.name
        (!) jmhCompileGeneratedClassesTask depends on build
        """.replaceIndent(ADDITIONAL_LOGGING_MESSAGE_INDENT), PURPLE_BOLD))

}