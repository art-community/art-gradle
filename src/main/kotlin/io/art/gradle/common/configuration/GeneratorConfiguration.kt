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

import io.art.gradle.common.constants.*
import org.gradle.api.Project
import org.gradle.internal.jvm.Jvm
import java.nio.file.Path
import java.time.Duration
import javax.inject.Inject

data class SourceSet(
        val languages: MutableSet<GeneratorLanguage>,
        val root: String,
        val classpath: String,
        val module: String,
)

open class GeneratorConfiguration @Inject constructor(project: Project) {
    var forJvm = false
        private set

    var workingDirectory: Path = project.rootProject.buildDir.resolve(GENERATOR).toPath()
        private set

    var module: String = project.name.capitalize()
        private set

    var watcherPeriod: Duration = DEFAULT_WATCHER_PERIOD
        private set

    var loggingToConsole = false
        private set

    var loggingToDirectory = true
        private set

    var loggingDirectory: Path = workingDirectory
        private set

    var version = MAIN_BRANCH
        private set

    var repositoryUrl: String = STABLE_MAVEN_REPOSITORY
        private set

    var localJarOverridingPath: Path? = null
        private set

    var directoryExclusions: MutableSet<String> = mutableSetOf()
        private set

    var jvmExecutable: Path = Jvm.current().javaExecutable.toPath()
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

    fun workingDirectory(path: Path) {
        workingDirectory = path
    }

    fun module(module: String) {
        this.module = module
    }

    fun version(version: String) {
        this.version = version
    }

    fun repository(url: String) {
        this.repositoryUrl = url
    }

    fun jvm(enabled: Boolean = true) {
        this.forJvm = enabled
    }

    fun useLocalJar(jar: Path) {
        localJarOverridingPath = jar
    }

    fun exclude(directory: String) {
        directoryExclusions.add(directory)
    }

    fun jvmExecutable(executable: Path) {
        this.jvmExecutable = executable
    }
}
