/*
 * ART Java
 *
 * Copyright 2019 ART
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.art.gradle.extension

import org.gradle.api.initialization.*
import org.jetbrains.kotlin.backend.common.*
import ru.art.gradle.constants.*
import ru.art.gradle.context.Context.settingsExtension
import java.io.*
import java.lang.System.*
import java.util.*

open class SettingsExtension(private val settings: Settings) {
    var projectsPaths = mutableListOf<String>()
        private set

    fun addProjectsPath(path: String) {
        if (File(path).exists() && File(path).isDirectory) {
            projectsPaths.add(path)
        }
    }

    fun addCurrentPath() = addProjectsPath(settings.settingsDir.absolutePath)

    fun importProjectPathsFromLocalProperties() = File(LOCAL_PROPERTIES).onlyIf({ exists() }, { file ->
        val properties = Properties()
        properties.load(file.inputStream())
        (properties[PROJECTS_PATH_PROPERTY] as String).split(COMMA).toTypedArray().forEach(this::addProjectsPath)
    })

    fun importProjectPathsfromEnvironmentVariable(variable: String) = getenv(variable)?.onlyIf({ isNotEmpty() }, { envVar -> envVar.split(COMMA).toTypedArray().forEach(this::addProjectsPath) })

    init {
        settingsExtension = this
    }
}