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
import ru.art.gradle.constants.configuration.defaults.DefaultRepositoryConfiguration.PASSWORD_PROPERTY
import ru.art.gradle.constants.configuration.defaults.DefaultRepositoryConfiguration.REPOSITORY_ID
import ru.art.gradle.constants.configuration.defaults.DefaultRepositoryConfiguration.URL_PROPERTY
import ru.art.gradle.constants.configuration.defaults.DefaultRepositoryConfiguration.USERNAME_PROPERTY

open class RepositoryConfiguration {
    var customConfigurable = false
    var jcenter = true
    var mavenCentral = true
    var gradlePluginPortal = false
    var urlParameter = URL_PROPERTY
    var usernameParameter = USERNAME_PROPERTY
    var passwordParameter = PASSWORD_PROPERTY
    var repositoryId = REPOSITORY_ID
    var configurationParameterMode = PROPERTY
        private set
    var repositoryType = ARTIFACTORY
        private set

    fun asProperties() {
        configurationParameterMode = PROPERTY
    }

    fun asValues() {
        configurationParameterMode = VALUE
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

    fun useNexus() {
        repositoryType = NEXUS
    }

    fun useArtifacotry() {
        repositoryType = ARTIFACTORY
    }
}