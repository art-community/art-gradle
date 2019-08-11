/*
 *    Copyright 2019 ART 
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

package ru.art.gradle

import org.gradle.api.*
import ru.art.gradle.configurator.project.*
import ru.art.gradle.constants.*
import ru.art.gradle.context.Context.setProjectContext
import ru.art.gradle.extension.*
import ru.art.gradle.git.*
import ru.art.gradle.logging.*


open class ProjectPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        attention("Start of ART project configuring")
        setProjectContext(extensions.create(ART_EXTENSION, ProjectExtension::class.java, this), loadGitRepository())
        extensions.create(AFTER_CONFIGURING_ACTION_EXTENSION, AfterConfiguringExtension::class.java, this)
        configureProject()
    }
}