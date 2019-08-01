package ru.art.loader

import org.gradle.api.Project
import org.jfrog.artifactory.client.ArtifactoryClientBuilder.create
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import ru.art.constants.*
import ru.art.context.Context.projectConfiguration
import ru.art.logging.error
import javax.xml.parsers.DocumentBuilderFactory.newInstance

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