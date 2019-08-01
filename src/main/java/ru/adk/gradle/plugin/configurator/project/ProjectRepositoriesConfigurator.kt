package ru.adk.gradle.plugin.configurator.project

import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import ru.adk.gradle.plugin.constants.ADDITIONAL_LOGGING_MESSAGE_INDENT
import ru.adk.gradle.plugin.constants.EMPTY_STRING
import ru.adk.gradle.plugin.constants.STAR
import ru.adk.gradle.plugin.context.Context.projectConfiguration
import ru.adk.gradle.plugin.logging.LogMessageColor.PURPLE_BOLD
import ru.adk.gradle.plugin.logging.message
import ru.adk.gradle.plugin.logging.success

fun Project.addRepository(url: String, username: String, password: String) = repositories.maven { maven ->
    maven.url = uri(url)
    maven.credentials { credentials ->
        credentials.username = username
        credentials.password = password
    }
    success("Added maven repository with parameters:\n" + message("""
        URL = $url
        Username = $username
        Password = ${EMPTY_STRING.padStart(password.length, STAR)}
    """.replaceIndent(ADDITIONAL_LOGGING_MESSAGE_INDENT), PURPLE_BOLD))
}

fun Project.addRepositoryFromProperties(): MavenArtifactRepository = addRepository(projectConfiguration().repositoryConfiguration.getUrlParameter(this) + "/${projectConfiguration().repositoryConfiguration.repositoryId}",
        projectConfiguration().repositoryConfiguration.getUsernameParameter(this),
        projectConfiguration().repositoryConfiguration.getPasswordParameter(this))