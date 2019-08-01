package ru.art

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import ru.art.configuration.SettingsConfiguration
import ru.art.configurator.settings.configureSettings
import ru.art.constants.ART_EXTENSION

open class SettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) = with(settings) {
        extensions.create(ART_EXTENSION, SettingsConfiguration::class.java, settings)
        settings.gradle.settingsEvaluated {
            configureSettings()
        }
    }
}

