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

package ru.art.gradle.configuration

import org.gradle.api.*
import org.gradle.api.artifacts.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.ArtMajorMinorVersion.*
import ru.art.gradle.constants.DependencyVersionSelectionMode.*
import ru.art.gradle.context.Context.projectConfiguration
import ru.art.gradle.dependency.Dependency
import javax.inject.*

open class DependencyVersionsConfiguration @Inject constructor(val project: Project) {
    val projectVersionTreeVersions: MutableMap<Dependency, String> = mutableMapOf()
    val majorMinorVersions: MutableMap<Dependency, ArtMajorMinorVersion> = mutableMapOf()
    val versionSelectionModes: MutableMap<Dependency, DependencyVersionSelectionMode> = mutableMapOf()

    fun byProjectVersionTree(group: String, artifact: String) {
        val dependency = Dependency(group, artifact)
        projectVersionTreeVersions[dependency] = project.version as String
    }

    fun byArtMajorMinor(group: String, artifact: String, selectionAction: ArtMajorVersionSelector.() -> Unit) {
        val majorVersionSelector = ArtMajorVersionSelector()
        selectionAction(majorVersionSelector)
        val dependency = Dependency(group, artifact)
        versionSelectionModes[dependency] = ART_MAJOR_MINOR
        majorMinorVersions[dependency] = majorVersionSelector.selectedVersion
    }

    fun byArtMajorMinor(group: String, artifact: String, minorVersion: ArtMajorMinorVersion) {
        val dependency = Dependency(group, artifact)
        versionSelectionModes[dependency] = ART_MAJOR_MINOR
        majorMinorVersions[dependency] = minorVersion
    }

    fun addDependencyVersion(dependency: Dependency, selectionMode: DependencyVersionSelectionMode, version: Any?) = when (selectionMode) {
        PROJECT_VERSION_TREE -> byProjectVersionTree(dependency.group, dependency.artifact)
        ART_MAJOR_MINOR -> byArtMajorMinor(dependency.group, dependency.artifact, version as ArtMajorMinorVersion)
    }
}

open class ArtMajorVersionSelector {
    var selectedVersion = ArtMajorMinorVersion.latest()
        private set

    fun art_1_0() {
        selectedVersion = RELEASE_1_0
    }

    fun latest() {
        selectedVersion = ArtMajorMinorVersion.latest()
    }
}

fun Project.byProjectVersionTree(dependency: ExternalModuleDependency) {
    dependency.group ?: return
    projectConfiguration().dependencyVersionsConfiguration.byProjectVersionTree(dependency.group!!, dependency.name)
}

fun Project.byArtMajorMinor(dependency: ExternalModuleDependency) {
    dependency.group ?: return
    projectConfiguration().dependencyVersionsConfiguration.byArtMajorMinor(dependency.group!!, dependency.name) { latest() }
}

fun Project.byArtMajorMinor(dependency: ExternalModuleDependency, selectionAction: ArtMajorVersionSelector.() -> Unit) {
    dependency.group ?: return
    projectConfiguration().dependencyVersionsConfiguration.byArtMajorMinor(dependency.group!!, dependency.name, selectionAction)
}

fun Project.byProjectVersionTree(dependency: Dependency) = projectConfiguration().dependencyVersionsConfiguration.byProjectVersionTree(dependency.group, dependency.artifact)

fun Project.byArtMajorMinor(dependency: Dependency) = projectConfiguration().dependencyVersionsConfiguration.byArtMajorMinor(dependency.group, dependency.artifact) { latest() }

fun Project.byArtMajorMinor(dependency: Dependency, selectionAction: ArtMajorVersionSelector.() -> Unit) = projectConfiguration().dependencyVersionsConfiguration.byArtMajorMinor(dependency.group, dependency.artifact, selectionAction)