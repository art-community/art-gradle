package ru.adk.gradle.plugin.configuration

import org.gradle.api.Project
import ru.adk.gradle.plugin.constants.ConfigurationParameterMode.PROPERTY
import ru.adk.gradle.plugin.constants.ConfigurationParameterMode.VALUE
import ru.adk.gradle.plugin.constants.RepositoryType.ARTIFACTORY
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultPublishingConfiguration.PASSWORD_PROPERTY
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultPublishingConfiguration.REPOSITORY_ID
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultPublishingConfiguration.URL_PROPERTY
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultPublishingConfiguration.USERNAME_PROPERTY
import ru.adk.gradle.plugin.context.Context.projectConfiguration
import javax.inject.Inject

open class PublishingConfiguration @Inject constructor(val project: Project) {
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