package ru.adk.gradle.plugin.configuration

import org.gradle.api.Project
import ru.adk.gradle.plugin.constants.ConfigurationParameterMode.PROPERTY
import ru.adk.gradle.plugin.constants.ConfigurationParameterMode.VALUE
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultRepositoryConfiguration.PASSWORD_PROPERTY
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultRepositoryConfiguration.REPOSITORY_ID
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultRepositoryConfiguration.URL_PROPERTY
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultRepositoryConfiguration.USERNAME_PROPERTY
import ru.adk.gradle.plugin.constants.RepositoryType.ARTIFACTORY
import ru.adk.gradle.plugin.constants.RepositoryType.NEXUS

open class RepositoryConfiguration {
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