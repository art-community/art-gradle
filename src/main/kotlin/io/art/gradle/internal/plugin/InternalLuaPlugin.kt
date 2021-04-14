package io.art.gradle.internal.plugin

import io.art.gradle.internal.configurator.configureLua
import io.art.gradle.internal.constants.LUA
import io.art.gradle.internal.extension.LuaExtension
import io.art.gradle.internal.logger.error
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

lateinit var luaPlugin: InternalLuaPlugin
    private set

class InternalLuaPlugin : Plugin<Project> {
    lateinit var extension: LuaExtension
        private set

    override fun apply(target: Project) {
        extension = target.extensions.create(LUA)
        luaPlugin = this
        target.runCatching(Project::configureLua).onFailure { error -> target.error(error) }
    }
}
