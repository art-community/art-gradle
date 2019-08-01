package ru.adk.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import ru.adk.gradle.plugin.configuration.SettingsConfiguration
import ru.adk.gradle.plugin.configurator.settings.configureSettings
import ru.adk.gradle.plugin.constants.ADK_EXTENSION

open class SettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) = with(settings) {
        extensions.create(ADK_EXTENSION, SettingsConfiguration::class.java, settings)
        settings.gradle.settingsEvaluated {
            configureSettings()
        }
    }
}

