package ru.art.configuration

import org.gradle.api.Action
import org.gradle.api.initialization.Settings
import org.jetbrains.kotlin.backend.common.onlyIf
import ru.art.constants.COMMA
import ru.art.constants.LOCAL_PROPERTIES
import ru.art.constants.PROJECTS_PATH_PROPERTY
import ru.art.context.Context.settingsConfiguration
import java.io.File
import java.lang.System.getenv
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