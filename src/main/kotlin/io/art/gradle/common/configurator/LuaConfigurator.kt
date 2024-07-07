/*
 * ART
 *
 * Copyright 2019-2022 ART
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

package io.art.gradle.common.configurator

import io.art.gradle.common.constants.*
import io.art.gradle.common.logger.logger
import io.art.gradle.common.service.touch
import io.art.gradle.common.service.writeContent
import io.art.gradle.internal.plugin.InternalLuaPlugin
import io.art.gradle.internal.plugin.luaPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.plugins.ide.idea.model.IdeaModel
import java.io.File.separator

fun Project.configureLua() {
    val logger = logger(project.name)
    val sourcesDirectory = projectDir.resolve(LUA_SOURCE_SET)
    val destinationDirectory = layout.buildDirectory.file(DESTINATION).get().asFile

    pluginManager.apply(IdeaPlugin::class.java)
    with(the<IdeaModel>()) { module { sourceDirs.add(sourcesDirectory) } }

    tasks.register(BUILD) {
        group = BUILD
        doLast {
            val sourcesString = sourcesDirectory
                    .walkTopDown()
                    .filter { file -> file.isFile && file.extension == LUA }
                    .map { file -> sourcesDirectory.toPath().relativize(file.toPath()).toString().removeSuffix(DOT_LUA).replace(separator, DOT) }
                    .toList()
            val amalg = InternalLuaPlugin::class.java.classLoader.getResourceAsStream(AMALG_RELATIVE_PATH)
                    ?.bufferedReader()
                    ?.readText()
                    ?: return@doLast
            delete(temporaryDir)
            copy {
                into(temporaryDir)
                from(sourcesDirectory)
            }
            val builtScript = destinationDirectory.toPath().touch().resolve("${project.name}$DOT_LUA")
            exec {
                commandLine(luaPlugin.configuration.executable)
                args(temporaryDir.resolve(AMALG_LUA).toPath().writeContent(amalg).toAbsolutePath().toString())
                args(LUA_OUTPUT_FLAG, builtScript)
                args(sourcesString)
                workingDir(temporaryDir)
                standardOutput = logger.output()
                errorOutput = logger.error()
            }
            if (luaPlugin.configuration.removeInitSuffix) {
                builtScript.writeContent(builtScript.toFile().readText().replace(DOT_INIT, EMPTY_STRING))
            }
            delete(temporaryDir)
        }
    }

    tasks.register(CLEAN) {
        group = BUILD
        doLast { delete(buildDir) }
    }
}
