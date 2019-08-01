package ru.adk.gradle.plugin.selector

import org.gradle.api.*
import org.gradle.api.artifacts.*
import ru.adk.gradle.plugin.constants.*
import ru.adk.gradle.plugin.constants.RepositoryType.*
import ru.adk.gradle.plugin.context.Context.projectConfiguration
import ru.adk.gradle.plugin.loader.*
import ru.adk.gradle.plugin.logging.*
import ru.adk.gradle.plugin.model.*
import kotlin.math.*

fun DependencyResolveDetails.selectVersionByBranch(version: String?, project: Project): String? {
    version ?: return EMPTY_STRING

    val versions = loadVersions(project)
    if (versions.isEmpty()) {
        return EMPTY_STRING
    }

    if (version == RELEASE_BRANCH) {
        return extractMaxTagVersion(versions)
    }

    if (!versions.contains(version)) {
        if (version == DEVELOPMENT_BRANCH) {
            return selectVersionByBranch(RELEASE_CANDIDATE_BRANCH, project)
        }

        if (version == RELEASE_CANDIDATE_BRANCH) {
            return selectVersionByBranch(RELEASE_BRANCH, project)
        }

        return selectVersionByBranch(DEVELOPMENT_BRANCH, project)
    }

    project.success("For dependency(${this.requested.group}:${this.requested.name}) select version '$version' by branch")
    return version
}

fun DependencyResolveDetails.selectVersionByTag(version: DependencyTagVersion?, project: Project): String {
    version ?: return EMPTY_STRING
    val taggedVersion = "${version.prefix}-${version.tag}"
    if (!loadVersions(project).contains(taggedVersion)) {
        return EMPTY_STRING
    }
    project.success("For dependency(${this.requested.group}:${this.requested.name}) select version '$taggedVersion' by tag '${version.tag}' with prefix '${version.prefix}'")
    return taggedVersion
}

fun DependencyResolveDetails.selectVersionByMajor(version: AdkMajorVersion?, project: Project): String? {
    version ?: return EMPTY_STRING
    val versions = loadVersions(project)
    if (versions.isEmpty()) {
        return EMPTY_STRING
    }
    val latestMajorVersion = "$RELEASE_TAG_PREFIX-${extractMaxTagVersion(versions, version.majorTag.toString())}"
    if (latestMajorVersion.isNotEmpty()) {
        project.success("For dependency(${this.requested.group}${this.requested.name}) select latest ADK major version: '$latestMajorVersion'")
    }
    return latestMajorVersion
}

fun DependencyResolveDetails.useManualVersionSelection(version: String?, project: Project): String? {
    version ?: return EMPTY_STRING
    if (!loadVersions(project).contains(version)) {
        return EMPTY_STRING
    }
    project.success("For dependency(${this.requested.group}:${this.requested.name}) manually select version: '$version'")
    return version
}

fun DependencyResolveDetails.useLatestVersionSelection(project: Project): String? {
    val versions = loadVersions(project)
    if (versions.isEmpty()) {
        return EMPTY_STRING
    }
    val version = "$RELEASE_TAG_PREFIX-${extractMaxTagVersion(versions)}"
    if (version.isNotEmpty()) {
        project.success("For dependency(${this.requested.group}${this.requested.name}) select latest version: '$version'")
    }
    return version
}

private fun DependencyResolveDetails.loadVersions(project: Project) = when (project.projectConfiguration().repositoryConfiguration.repositoryType) {
    NEXUS -> TODO()
    ARTIFACTORY -> loadDependencyVersionsFromArtifactory(requested.module.group, requested.module.name, project)
}

private fun extractMaxTagVersion(versions: List<String>, majorTag: String = EMPTY_STRING): String {
    var tags = versions.filter { version -> version.matches(TAG_PATTERN(RELEASE_TAG_PREFIX)) }
    if (tags.isEmpty()) {
        return EMPTY_STRING
    }
    if (majorTag.isNotEmpty()) {
        tags = tags.filter { tag -> tag.matches(MAJOR_RELEASE_PATTERN(majorTag)) }
    }
    return tags
            .map { TAG_PATTERN(RELEASE_TAG_PREFIX).matchEntire(it)!!.groupValues[1] }
            .maxWith(Comparator { current, next ->
                val currentParts = current.split(DOT)
                val nextParts = next.split(DOT)
                val commonIndices = min(currentParts.size, nextParts.size)
                repeat(commonIndices) { i ->
                    val currentPart = currentParts[i].toInt()
                    val nextPart = nextParts[i].toInt()
                    if (currentPart != nextPart) {
                        return@Comparator currentPart.compareTo(nextPart)
                    }
                }
                currentParts.size.compareTo(nextParts.size)
            })!!
}