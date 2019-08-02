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

package ru.art.gradle.loader

import org.gradle.api.*
import org.jfrog.artifactory.client.ArtifactoryClientBuilder.*
import org.w3c.dom.*
import ru.art.gradle.constants.*
import ru.art.gradle.context.Context.projectConfiguration
import ru.art.gradle.logging.*
import javax.xml.parsers.DocumentBuilderFactory.*

fun loadDependencyVersionsFromArtifactory(dependencyGroup: String, dependencyName: String, project: Project) = try {
    create()
            .run {
                url = project.projectConfiguration().repositoryConfiguration.getUrlParameter(project)
                username = project.projectConfiguration().repositoryConfiguration.getUsernameParameter(project)
                password = project.projectConfiguration().repositoryConfiguration.getPasswordParameter(project)
                build()
            }
            .repository(project.projectConfiguration().repositoryConfiguration.repositoryId)
            .download("${dependencyGroup.replace(DOT, SLASH)}/$dependencyName/$MAVEN_METADATA_XML")
            .doDownload()
            .let { content ->
                newInstance()
                        .newDocumentBuilder()
                        .parse(content)
                        .getElementsByTagName(VERSIONING_TAG)
                        .toList()
                        .flatMap { versioningNode -> versioningNode.childNodes.toList { nodeName == VERSIONS_TAG } }
                        .flatMap { versionsNode -> versionsNode.childNodes.toList { nodeName == VERSION_TAG } }
                        .map { versionNode -> versionNode.textContent }
            }
} catch (e: Exception) {
    project.error(e.toString())
    emptyList<String>()
}


private fun NodeList.toList(predicate: Node.() -> Boolean = { true }): MutableList<Node> {
    val versions = mutableListOf<Node>()
    repeat(length) { id ->
        val item = item(id)
        if (predicate(item)) {
            versions.add(item)
        }
    }
    return versions
}