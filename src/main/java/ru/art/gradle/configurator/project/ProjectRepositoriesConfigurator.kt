package ru.art.gradle.configurator.project

import org.gradle.api.*
import org.gradle.api.artifacts.repositories.*
import ru.art.gradle.constants.*
import ru.art.gradle.context.Context.projectConfiguration
import ru.art.gradle.logging.*
import ru.art.gradle.logging.LogMessageColor.*

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