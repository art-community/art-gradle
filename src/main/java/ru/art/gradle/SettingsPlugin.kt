package ru.art.gradle

import org.gradle.api.*
import org.gradle.api.initialization.*
import ru.art.gradle.configuration.*
import ru.art.gradle.configurator.settings.*
import ru.art.gradle.constants.*

open class SettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) = with(settings) {
        extensions.create(ART_EXTENSION, SettingsConfiguration::class.java, settings)
        settings.gradle.settingsEvaluated {
            configureSettings()
        }
    }
}

