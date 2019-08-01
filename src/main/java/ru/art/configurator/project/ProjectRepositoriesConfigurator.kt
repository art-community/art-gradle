package ru.art.configurator.project

import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import ru.art.constants.ADDITIONAL_LOGGING_MESSAGE_INDENT
import ru.art.constants.EMPTY_STRING
import ru.art.constants.STAR
import ru.art.context.Context.projectConfiguration
import ru.art.logging.LogMessageColor.PURPLE_BOLD
import ru.art.logging.message
import ru.art.logging.success

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