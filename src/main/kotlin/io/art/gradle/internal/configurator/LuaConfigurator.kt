package io.art.gradle.internal.configurator

import io.art.gradle.common.service.touch
import io.art.gradle.common.service.writeContent
import io.art.gradle.internal.constants.*
import io.art.gradle.internal.logger.attention
import io.art.gradle.internal.logger.logger
import io.art.gradle.internal.plugin.InternalLuaPlugin
import io.art.gradle.internal.plugin.luaPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.plugins.ide.idea.model.IdeaModel
import java.io.File.separator

fun Project.configureLua() {
    val sourcesDirectory = projectDir.resolve(LUA_SOURCE_SET)
    val destinationDirectory = buildDir.resolve(DESTINATION)
    pluginManager.apply(IdeaPlugin::class.java)
    with(the<IdeaModel>()) {
        module {
            sourceDirs.add(sourcesDirectory)
        }
    }
    tasks.register(BUILD) {
        group = ART
        doLast {
            val sourcesString = sourcesDirectory
                    .walkTopDown()
                    .filter { file -> file.isFile && file.extension == LUA }
                    .map { file -> sourcesDirectory.toPath().relativize(file.toPath()).toString().removeSuffix(DOT_LUA).replace(separator, DOT) }
                    .toList()
            val bundler = InternalLuaPlugin::class.java.classLoader.getResourceAsStream(BUNDLER_RELATIVE_PATH)
                    ?.bufferedReader()
                    ?.readText()
                    ?: return@doLast
            delete(temporaryDir)
            copy {
                into(temporaryDir)
                from(sourcesDirectory)
            }
            val bundleScript = destinationDirectory.toPath().touch().resolve("${project.name}$DOT_LUA")
            exec {
                commandLine(luaPlugin.extension.executable)
                val bundlerScript = temporaryDir.resolve(BUNDLER_NAME).toPath().writeContent(bundler).toAbsolutePath().toString()
                args(bundlerScript, "-o", bundleScript)
                args(sourcesString)
                workingDir(temporaryDir)
                standardOutput = logger(project.name).output()
                errorOutput = logger(project.name).error()
            }
            bundleScript.writeContent(bundleScript.toFile().readText().replace(".init", ""))
            attention("Built bundled Lua script: $bundleScript")
        }
    }
}
