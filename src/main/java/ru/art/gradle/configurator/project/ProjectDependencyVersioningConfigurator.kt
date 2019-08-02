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
import org.gradle.api.artifacts.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.DependencyVersionSelectionMode.*
import ru.art.gradle.context.Context.projectConfiguration
import ru.art.gradle.dependency.Dependency
import ru.art.gradle.exception.*
import ru.art.gradle.logging.*
import ru.art.gradle.selector.*

private val Project.VERSIONS_CACHE: MutableMap<Dependency, String> get() = mutableMapOf()

fun Project.calculateDependencyVersions() {
    projectConfiguration()
            .dependencyVersionsConfiguration
            .versionSelectionModes
            .keys
            .forEach { dependency -> additionalAttention("Resolving version for dependency(${dependency.inGradleNotation()})") }
    configurations.filter { it.name in RESOLVABLE_CONFIGURATIONS }.forEach(this::resolveConfiguration)
}

private fun Project.resolveConfiguration(configuration: Configuration) {
    configuration.resolutionStrategy.eachDependency { dependencyDetails ->
        with(dependencyDetails) {
            requested ?: return@eachDependency
            requested.module ?: return@eachDependency
            requested.module.group ?: return@eachDependency
            requested.module.name ?: return@eachDependency
            val dependency = Dependency(requested.group, requested.name)
            val versionFromCache = VERSIONS_CACHE[dependency]
            if (versionFromCache != null) {
                success("For dependency(${dependency.inGradleNotation()}) using version from cache: '$versionFromCache'")
                useVersion(versionFromCache)
                return@eachDependency
            }
            with(projectConfiguration().dependencyVersionsConfiguration) {
                if (requested.version.isNullOrEmpty() &&
                        !projectConfiguration().dependencySubstitutionConfiguration.codeSubstitutions.contains(dependency)) {
                    val version = when (versionSelectionModes[dependency]) {
                        BRANCH -> selectVersionByBranch(versionsByBranch[dependency], this@resolveConfiguration)
                        TAG -> selectVersionByTag(versionsByTag[dependency], this@resolveConfiguration)
                        MANUAL -> useManualVersionSelection(manualVersions[dependency], this@resolveConfiguration)
                        LATEST -> useLatestVersionSelection(this@resolveConfiguration)
                        MAJOR -> selectVersionByMajor(majorVersions[dependency], this@resolveConfiguration)
                        null -> return@eachDependency
                    }
                    if (version.isNullOrBlank()) {
                        error("Unable to resolve version for dependency(${dependency.inGradleNotation()})")
                        return@eachDependency
                    }
                    VERSIONS_CACHE[dependency] = version
                    success("For dependency(${dependency.inGradleNotation()}) use resolved version: '$version'")
                    useVersion(version)
                }
            }
        }
    }
    ignoreException { configuration.resolve() }
}