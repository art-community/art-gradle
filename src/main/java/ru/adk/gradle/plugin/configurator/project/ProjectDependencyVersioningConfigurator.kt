package ru.adk.gradle.plugin.configurator.project

import org.gradle.api.*
import org.gradle.api.artifacts.*
import ru.adk.gradle.plugin.constants.*
import ru.adk.gradle.plugin.constants.DependencyVersionSelectionMode.*
import ru.adk.gradle.plugin.context.Context.projectConfiguration
import ru.adk.gradle.plugin.dependency.Dependency
import ru.adk.gradle.plugin.exception.*
import ru.adk.gradle.plugin.logging.*
import ru.adk.gradle.plugin.selector.*

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