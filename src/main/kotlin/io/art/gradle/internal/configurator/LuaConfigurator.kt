package io.art.gradle.internal.configurator

import io.art.gradle.common.constants.EMPTY_STRING
import io.art.gradle.common.logger.logger
import io.art.gradle.common.service.touch
import io.art.gradle.common.service.writeContent
import io.art.gradle.internal.constants.*
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
    val destinationDirectory = buildDir.resolve(DESTINATION)

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
                commandLine(luaPlugin.extension.executable)
                args(temporaryDir.resolve(AMALG_LUA).toPath().writeContent(amalg).toAbsolutePath().toString())
                args("-o", builtScript)
                args(sourcesString)
                workingDir(temporaryDir)
                standardOutput = logger.output()
                errorOutput = logger.error()
            }
            if (luaPlugin.extension.removeInitSuffix) {
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
