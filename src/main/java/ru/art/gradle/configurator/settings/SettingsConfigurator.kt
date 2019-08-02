package ru.art.gradle.configurator.settings

import org.gradle.api.initialization.*

fun Settings.configureSettings() {
    configurePluginManagement()
    includeProjects()
}