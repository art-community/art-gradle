package ru.art.gradle.configuration

import org.gradle.api.*
import org.gradle.api.initialization.*
import org.jetbrains.kotlin.backend.common.*
import ru.art.gradle.constants.*
import ru.art.gradle.context.Context.settingsConfiguration
import java.io.*
import java.lang.System.*
import java.util.*

open class SettingsConfiguration(private val settings: Settings) {
    var projectsPaths = mutableListOf<String>()
        private set
    var repositoryConfiguration: SettingsRepositoryConfiguration = SettingsRepositoryConfiguration()
        private set


    fun addProjectsPath(path: String) {
        if (File(path).exists() && File(path).isDirectory) {
            projectsPaths.add(path)
        }
    }

    fun addCurrentPath() = addProjectsPath(settings.settingsDir.absolutePath)

    fun importProjectPathsfromLocalProperties() = File(LOCAL_PROPERTIES).onlyIf({ exists() }, { file ->
        val properties = Properties()
        properties.load(file.inputStream())
        (properties[PROJECTS_PATH_PROPERTY] as String).split(COMMA).toTypedArray().forEach(this::addProjectsPath)
    })

    fun importProjectPathsfromEnvironmentVariable(variable: String) = getenv(variable)?.onlyIf({ isNotEmpty() }, { envVar -> envVar.split(COMMA).toTypedArray().forEach(this::addProjectsPath) })

    fun repository(action: Action<in SettingsRepositoryConfiguration>) {
        action.execute(repositoryConfiguration)
    }

    init {
        settingsConfiguration = this
    }
}