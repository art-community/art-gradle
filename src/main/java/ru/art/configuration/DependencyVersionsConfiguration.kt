package ru.art.configuration

import org.gradle.api.*
import org.gradle.api.artifacts.*
import ru.art.constants.*
import ru.art.constants.ARTMajorVersion.*
import ru.art.constants.ConfigurationParameterMode.*
import ru.art.constants.DependencyVersionSelectionMode.*
import ru.art.context.Context.git
import ru.art.context.Context.projectConfiguration
import ru.art.dependency.Dependency
import ru.art.model.*
import javax.inject.*

open class DependencyVersionsConfiguration @Inject constructor(val project: Project) {
    val versionsByBranch: MutableMap<Dependency, String> = mutableMapOf()
    val versionsByTag: MutableMap<Dependency, DependencyTagVersion> = mutableMapOf()
    val manualVersions: MutableMap<Dependency, String> = mutableMapOf()
    val majorVersions: MutableMap<Dependency, ARTMajorVersion> = mutableMapOf()
    val versionSelectionModes: MutableMap<Dependency, DependencyVersionSelectionMode> = mutableMapOf()

    fun byBranch(group: String, artifact: String) {
        val dependency = Dependency(group, artifact)
        with(project.projectConfiguration().projectVersionConfiguration) {
            versionsByBranch[dependency] = when (branchParameterMode) {
                VALUE -> branchParameter!!
                PROPERTY -> project.properties[branchParameter] as? String ?: return
                null -> project.git()?.repository?.branch ?: return
            }
        }
        versionSelectionModes[dependency] = BRANCH
    }

    fun byTag(group: String, artifact: String, tag: String) {
        val dependency = Dependency(group, artifact)
        versionsByTag[dependency] = DependencyTagVersion(tag, RELEASE_TAG_PREFIX)
        versionSelectionModes[dependency] = TAG
    }

    fun byTag(group: String, artifact: String, tag: DependencyTagVersion) {
        val dependency = Dependency(group, artifact)
        versionsByTag[dependency] = tag
        versionSelectionModes[dependency] = TAG
    }

    fun useVersion(group: String, artifact: String, version: String) {
        val dependency = Dependency(group, artifact)
        manualVersions[dependency] = version
        versionSelectionModes[dependency] = MANUAL
    }

    fun useLatest(group: String, artifact: String) {
        versionSelectionModes[Dependency(group, artifact)] = LATEST
    }

    fun byMajor(group: String, artifact: String, selectionAction: MajorVersionSelector.() -> Unit) {
        val majorVersionSelector = MajorVersionSelector()
        selectionAction(majorVersionSelector)
        val dependency = Dependency(group, artifact)
        versionSelectionModes[dependency] = MAJOR
        majorVersions[dependency] = majorVersionSelector.selectedVersion
    }

    fun byMajor(group: String, artifact: String, version: ARTMajorVersion) {
        val dependency = Dependency(group, artifact)
        versionSelectionModes[dependency] = MAJOR
        majorVersions[dependency] = version
    }

    fun addDependencyVersion(dependency: Dependency, selectionMode: DependencyVersionSelectionMode, version: Any?) = when (selectionMode) {
        BRANCH -> byBranch(dependency.group, dependency.artifact)
        TAG -> byTag(dependency.group, dependency.artifact, version as DependencyTagVersion)
        MANUAL -> useVersion(dependency.group, dependency.artifact, version as String)
        MAJOR -> byMajor(dependency.group, dependency.artifact, version as ARTMajorVersion)
        LATEST -> useLatest(dependency.group, dependency.artifact)
    }
}

open class MajorVersionSelector {
    var selectedVersion = ARTMajorVersion.latest()
        private set

    fun art1() {
        selectedVersion = RELEASE_1
    }

    fun latest() {
        selectedVersion = ARTMajorVersion.latest()
    }
}

fun Project.byBranch(dependency: ExternalModuleDependency) {
    dependency.group ?: return
    projectConfiguration().dependencyVersionsConfiguration.byBranch(dependency.group!!, dependency.name)
}

fun Project.byTag(dependency: ExternalModuleDependency, tag: String) {
    dependency.group ?: return
    projectConfiguration().dependencyVersionsConfiguration.byTag(dependency.group!!, dependency.name, tag)
}

fun Project.byTag(dependency: ExternalModuleDependency, tag: DependencyTagVersion) {
    dependency.group ?: return
    projectConfiguration().dependencyVersionsConfiguration.byTag(dependency.group!!, dependency.name, tag)
}

fun Project.useLatest(dependency: ExternalModuleDependency) {
    dependency.group ?: return
    projectConfiguration().dependencyVersionsConfiguration.useLatest(dependency.group!!, dependency.name)
}

fun Project.byMajor(dependency: ExternalModuleDependency) {
    dependency.group ?: return
    projectConfiguration().dependencyVersionsConfiguration.byMajor(dependency.group!!, dependency.name) { latest() }
}

fun Project.byMajor(dependency: ExternalModuleDependency, selectionAction: MajorVersionSelector.() -> Unit) {
    dependency.group ?: return
    projectConfiguration().dependencyVersionsConfiguration.byMajor(dependency.group!!, dependency.name, selectionAction)
}

fun Project.byBranch(dependency: Dependency) = projectConfiguration().dependencyVersionsConfiguration.byBranch(dependency.group, dependency.artifact)

fun Project.byTag(dependency: Dependency, tag: String) = projectConfiguration().dependencyVersionsConfiguration.byTag(dependency.group, dependency.artifact, tag)

fun Project.byTag(dependency: Dependency, tag: DependencyTagVersion) = projectConfiguration().dependencyVersionsConfiguration.byTag(dependency.group, dependency.artifact, tag)

fun Project.useLatest(dependency: Dependency) = projectConfiguration().dependencyVersionsConfiguration.useLatest(dependency.group, dependency.artifact)

fun Project.byMajor(dependency: Dependency) = projectConfiguration().dependencyVersionsConfiguration.byMajor(dependency.group, dependency.artifact) { latest() }

fun Project.byMajor(dependency: Dependency, selectionAction: MajorVersionSelector.() -> Unit) = projectConfiguration().dependencyVersionsConfiguration.byMajor(dependency.group, dependency.artifact, selectionAction)