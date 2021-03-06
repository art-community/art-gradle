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

import org.gradle.api.Project
import org.gradle.api.internal.project.DefaultProject
import ru.art.gradle.constants.COMPILE_TASK_PREFIX
import ru.art.gradle.constants.DefaultTasks.BUILD
import ru.art.gradle.context.Context.projectExtension
import ru.art.gradle.dependency.Dependency
import ru.art.gradle.logging.success
import ru.art.gradle.logging.warning

fun Project.substituteDependencies() {
    val substitutedDependencies = mutableSetOf<Dependency>()
    projectExtension().dependencySubstitutionConfiguration.codeSubstitutions
            .forEach { dependency ->
                configurations.all { configuration ->
                    try {
                        findProject(":${dependency.artifact}") ?: return@all
                        val dependencyProject = project(":${dependency.artifact}") as DefaultProject
                        if (dependencyProject.state.isUnconfigured) {
                            dependencyProject.evaluate()
                        }
                        substitutedDependencies.add(dependency)
                        val module = configuration.resolutionStrategy.dependencySubstitution.module(dependency.inGradleNotation())
                        val project = configuration.resolutionStrategy.dependencySubstitution.project(":${dependency.artifact}")
                        configuration.resolutionStrategy.dependencySubstitution.substitute(module).with(project)
                        tasks.filter { task -> task.name.startsWith(COMPILE_TASK_PREFIX) }
                                .forEach { it.dependsOn(":${dependency.artifact}:$BUILD") }
                    } catch (throwable: Throwable) {
                        throwable.message?.let { message -> warning(message) }
                    }
                }
            }
    substitutedDependencies.forEach { substitutedDependency ->
        success("Substitute dependency(${substitutedDependency.inGradleNotation()}) with project(:${substitutedDependency.artifact}) for all configurations")

    }
}