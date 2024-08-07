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

package io.art.gradle.common.configuration

import io.art.gradle.common.constants.TEST_EXECUTABLE
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import java.nio.file.Path
import javax.inject.Inject

open class TestConfiguration @Inject constructor(project: Project, objectFactory: ObjectFactory) {
    var launcherClass: String? = null
        private set
    var executableName: String = project.name
        private set
    var directory: Path = project.layout.buildDirectory.file(TEST_EXECUTABLE).get().asFile.toPath()
        private set

    val jar = objectFactory.newInstance<JarExecutableConfiguration>()

    var jarEnabled = false
        private set

    fun name(name: String) {
        this.executableName = name
    }

    fun directory(directory: Path) {
        this.directory = directory
    }

    fun jar(action: Action<in JarExecutableConfiguration> = Action { }) {
        action.execute(jar)
        jarEnabled = true
    }

    fun launcher(launcherClass: String) {
        this.launcherClass = launcherClass
    }
}
