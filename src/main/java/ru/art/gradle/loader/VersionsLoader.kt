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