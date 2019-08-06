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

package ru.art.gradle.configuration

import org.gradle.api.*
import ru.art.gradle.constants.ConfigurationParameterMode.*
import ru.art.gradle.constants.RepositoryType.*
import ru.art.gradle.constants.configuration.defaults.DefaultPublishingConfiguration.PASSWORD_PROPERTY
import ru.art.gradle.constants.configuration.defaults.DefaultPublishingConfiguration.REPOSITORY_ID
import ru.art.gradle.constants.configuration.defaults.DefaultPublishingConfiguration.URL_PROPERTY
import ru.art.gradle.constants.configuration.defaults.DefaultPublishingConfiguration.USERNAME_PROPERTY
import ru.art.gradle.context.Context.projectConfiguration
import javax.inject.*

open class PublishingConfiguration @Inject constructor(val project: Project) {
    var enabled = false
    var urlParameter = URL_PROPERTY
    var usernameParameter = USERNAME_PROPERTY
    var passwordParameter = PASSWORD_PROPERTY
    var repositoryId = REPOSITORY_ID
    var repositoryType = ARTIFACTORY
        private set
    var configurationParameterMode = PROPERTY
        private set

    fun asProperties() {
        configurationParameterMode = PROPERTY
    }

    fun asValues() {
        configurationParameterMode = VALUE
    }

    fun fromRepositoryConfiguration() {
        urlParameter = project.projectConfiguration().repositoryConfiguration.urlParameter
        usernameParameter = project.projectConfiguration().repositoryConfiguration.usernameParameter
        passwordParameter = project.projectConfiguration().repositoryConfiguration.passwordParameter
        configurationParameterMode = project.projectConfiguration().repositoryConfiguration.configurationParameterMode
        repositoryId = project.projectConfiguration().repositoryConfiguration.repositoryId
        repositoryType = project.projectConfiguration().repositoryConfiguration.repositoryType
    }

    fun getUrlParameter(project: Project) = when (configurationParameterMode) {
        PROPERTY -> project.properties[urlParameter] as String
        VALUE -> urlParameter
    }

    fun getUsernameParameter(project: Project) = when (configurationParameterMode) {
        PROPERTY -> project.properties[usernameParameter] as String
        VALUE -> usernameParameter
    }

    fun getPasswordParameter(project: Project) = when (configurationParameterMode) {
        PROPERTY -> project.properties[passwordParameter] as String
        VALUE -> passwordParameter
    }
}