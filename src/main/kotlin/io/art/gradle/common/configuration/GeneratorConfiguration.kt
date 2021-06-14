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

package io.art.gradle.common.configuration

import io.art.gradle.common.constants.DEFAULT_WATCHER_PERIOD
import io.art.gradle.common.constants.GENERATOR
import io.art.gradle.common.constants.GeneratorLanguage
import io.art.gradle.common.constants.GeneratorLanguage.*
import io.art.gradle.common.constants.MODULE_YML
import org.gradle.api.Project
import java.io.File
import java.nio.file.Path
import java.time.Duration
import javax.inject.Inject

open class GeneratorConfiguration @Inject constructor(project: Project) {
    var configurationPath: Path = project.rootProject.buildDir.resolve(GENERATOR).resolve(MODULE_YML).toPath()
        private set

    var watcherPeriod: Duration = DEFAULT_WATCHER_PERIOD
        private set

    var loggingToConsole = false
        private set

    var loggingToDirectory = true
        private set

    var loggingDirectory: Path = project.rootProject.buildDir.resolve(GENERATOR).toPath()
        private set

    var sourceSets = mutableMapOf<GeneratorLanguage, MutableSet<Path>>()
        private set

    fun watcherPeriod(period: Duration) {
        watcherPeriod = period
    }

    fun consoleLogging() {
        loggingToConsole = true
        loggingToDirectory = false
    }

    fun fileLogging(directory: Path) {
        loggingDirectory = directory
        loggingToConsole = false
        loggingToDirectory = true
    }

    fun configurationPath(path: Path) {
        configurationPath = path
    }

    fun java(source: File) {
        sourceSets.putIfAbsent(JAVA, mutableSetOf(source.toPath()))?.add(source.toPath())
    }

    fun kotlin(source: File) {
        sourceSets.putIfAbsent(KOTLIN, mutableSetOf(source.toPath()))?.add(source.toPath())
    }
}
