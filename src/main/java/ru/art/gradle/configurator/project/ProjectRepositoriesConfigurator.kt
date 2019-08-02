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