package ru.adk.gradle.plugin.configurator.settings

import org.gradle.api.initialization.Settings

fun Settings.configureSettings() {
    configurePluginManagement()
    includeProjects()
}