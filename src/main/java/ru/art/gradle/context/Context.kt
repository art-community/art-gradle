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

package ru.art.gradle.context

import org.eclipse.jgit.api.*
import org.gradle.api.*
import ru.art.gradle.extension.*
import java.util.concurrent.*

object Context {
    val projectsContext = ConcurrentHashMap<String, ProjectContext>()
    @Volatile
    lateinit var settingsExtension: SettingsExtension

    fun Project.projectExtension() = projectsContext[name]!!.projectExtension

    fun Project.git() = projectsContext[name]!!.git

    fun Project.auxiliaryInformation() = projectsContext[name]!!.projectAuxiliaryInformation

    fun Project.setProjectContext(projectExtension: ProjectExtension, git: Git?) {
        projectsContext[name] = ProjectContext(projectExtension = projectExtension,
                git = git,
                projectAuxiliaryInformation = ProjectAuxiliaryInformation())
    }

    fun Project.runAfterConfiguringAction() = projectsContext[name]?.afterConfiguringAction?.execute(projectsContext[name]!!)


    data class ProjectContext(val projectExtension: ProjectExtension,
                              val git: Git? = null,
                              val projectAuxiliaryInformation: ProjectAuxiliaryInformation,
                              var afterConfiguringAction: Action<in ProjectContext> = Action {})

    data class ProjectAuxiliaryInformation(var hasGroovy: Boolean = false,
                                           var hasScala: Boolean = false,
                                           var hasKotlin: Boolean = false,
                                           var hasGatling: Boolean = false,
                                           var hasJmh: Boolean = false,
                                           var hasWeb: Boolean = false,
                                           var hasGroovyTests: Boolean = false,
                                           var hasScalaTests: Boolean = false,
                                           var hasKotlinTests: Boolean = false,
                                           var hasCheckstyle: Boolean = false,
                                           var hasProtobufGenerator: Boolean = false,
                                           var hasGenerator: Boolean = false,
                                           var hasLombok: Boolean = false,
                                           var hasSpock: Boolean = false
    )
}