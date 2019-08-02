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

import org.gradle.api.initialization.*
import ru.art.gradle.constants.ConfigurationParameterMode.*
import ru.art.gradle.constants.RepositoryType.*
import ru.art.gradle.constants.configuration.defaults.DefaultRepositoryConfiguration.PASSWORD_PROPERTY
import ru.art.gradle.constants.configuration.defaults.DefaultRepositoryConfiguration.REPOSITORY_ID
import ru.art.gradle.constants.configuration.defaults.DefaultRepositoryConfiguration.URL_PROPERTY
import ru.art.gradle.constants.configuration.defaults.DefaultRepositoryConfiguration.USERNAME_PROPERTY
import java.io.*
import java.util.*

open class SettingsRepositoryConfiguration {
    var urlParameter = URL_PROPERTY
    var usernameParameter = USERNAME_PROPERTY
    var passwordParameter = PASSWORD_PROPERTY
    var repositoryId = REPOSITORY_ID
    var configurationParameterMode = PROPERTY
        private set
    var repositoryType = ARTIFACTORY
        private set

    init {
    }

    fun asProperties() {
        configurationParameterMode = PROPERTY
    }

    fun asValues() {
        configurationParameterMode = VALUE
    }

    fun getUrlParameter(settings: Settings) = when (configurationParameterMode) {
        PROPERTY -> loadProperties(settings)[urlParameter] as String
        VALUE -> urlParameter
    }

    fun getUsernameParameter(settings: Settings) = when (configurationParameterMode) {
        PROPERTY -> loadProperties(settings)[usernameParameter] as String
        VALUE -> usernameParameter
    }

    fun getPasswordParameter(settings: Settings) = when (configurationParameterMode) {
        PROPERTY -> loadProperties(settings)[passwordParameter] as String
        VALUE -> passwordParameter
    }

    fun useNexus() {
        repositoryType = NEXUS
    }

    fun useArtifacotry() {
        repositoryType = ARTIFACTORY
    }

    private fun loadProperties(settings: Settings) = Properties().apply {
        load(File("${settings.gradle.gradleUserHomeDir.absolutePath}/gradle.properties").inputStream())
    }
}