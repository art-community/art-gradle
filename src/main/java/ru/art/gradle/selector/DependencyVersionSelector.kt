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

package ru.art.gradle.selector

import org.gradle.api.*
import org.gradle.api.artifacts.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.RepositoryType.*
import ru.art.gradle.context.Context.projectConfiguration
import ru.art.gradle.loader.*
import ru.art.gradle.logging.*
import ru.art.gradle.model.*
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

fun DependencyResolveDetails.selectVersionByMajor(version: ARTMajorVersion?, project: Project): String? {
    version ?: return EMPTY_STRING
    val versions = loadVersions(project)
    if (versions.isEmpty()) {
        return EMPTY_STRING
    }
    val latestMajorVersion = "$RELEASE_TAG_PREFIX-${extractMaxTagVersion(versions, version.majorTag.toString())}"
    if (latestMajorVersion.isNotEmpty()) {
        project.success("For dependency(${this.requested.group}${this.requested.name}) select latest ART major version: '$latestMajorVersion'")
    }
    return latestMajorVersion
}

fun DependencyResolveDetails.useManualVersionSelection(version: String?, project: Project): String? {
    version ?: return EMPTY_STRING
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