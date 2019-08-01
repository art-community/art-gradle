package ru.art.configuration

import org.gradle.api.initialization.*
import ru.art.constants.ConfigurationParameterMode.*
import ru.art.constants.RepositoryType.*
import ru.art.constants.configuration.defaults.DefaultRepositoryConfiguration.PASSWORD_PROPERTY
import ru.art.constants.configuration.defaults.DefaultRepositoryConfiguration.REPOSITORY_ID
import ru.art.constants.configuration.defaults.DefaultRepositoryConfiguration.URL_PROPERTY
import ru.art.constants.configuration.defaults.DefaultRepositoryConfiguration.USERNAME_PROPERTY
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