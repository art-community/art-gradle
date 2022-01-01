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

package io.art.gradle.internal.plugin

import io.art.gradle.common.configurator.configureLua
import io.art.gradle.common.constants.LUA
import io.art.gradle.common.configuration.LuaConfiguration
import io.art.gradle.common.logger.error
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

lateinit var luaPlugin: InternalLuaPlugin
    private set

class InternalLuaPlugin : Plugin<Project> {
    lateinit var configuration: LuaConfiguration
        private set

    override fun apply(target: Project) {
        configuration = target.extensions.create(LUA)
        luaPlugin = this
        target.runCatching(Project::configureLua).onFailure { error -> target.error(error) }
    }
}
