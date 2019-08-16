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

package ru.art.gradle.configuration

import org.gradle.api.*
import org.gradle.api.artifacts.*
import ru.art.gradle.context.Context.projectExtension
import ru.art.gradle.dependency.Dependency
import javax.inject.*

open class DependencySubstitutionConfiguration @Inject constructor(val project: Project) {
    var codeSubstitutions: MutableSet<Dependency> = mutableSetOf()
        private set
    var artifactSubstitutions: MutableSet<Dependency> = mutableSetOf()
        private set

    fun substituteWithCode(group: String, artifact: String) {
        substituteWithCode(Dependency(group = group, artifact = artifact))
    }

    fun substituteWithArtifact(group: String, artifact: String) {
        substituteWithArtifact(Dependency(group = group, artifact = artifact))
    }

    fun substituteWithCode(dependency: Dependency) {
        project.findProject(":${dependency.artifact}") ?: return
        codeSubstitutions.add(dependency)
        artifactSubstitutions.remove(dependency)
    }

    fun substituteWithArtifact(dependency: Dependency) {
        codeSubstitutions.remove(dependency)
        artifactSubstitutions.add(dependency)
    }
}

fun Project.substituteWithCode(dependency: ExternalModuleDependency) {
    projectExtension()
            .dependencySubstitutionConfiguration
            .substituteWithCode(dependency.group ?: return, dependency.name)
}

fun Project.substituteWithArtifact(dependency: ExternalModuleDependency) {
    projectExtension()
            .dependencySubstitutionConfiguration
            .substituteWithArtifact(dependency.group ?: return, dependency.name)
}

fun Project.substituteWithCode(dependency: Dependency) = projectExtension().dependencySubstitutionConfiguration.substituteWithCode(dependency.group, dependency.artifact)

fun Project.substituteWithArtifact(dependency: Dependency) = projectExtension().dependencySubstitutionConfiguration.substituteWithArtifact(dependency.group, dependency.artifact)