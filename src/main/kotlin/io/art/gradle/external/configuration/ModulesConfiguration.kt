/*
 * ART
 *
 * Copyright 2019-2021 ART
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

package io.art.gradle.external.configuration

import io.art.gradle.external.constants.ArtVersion.MAIN
import io.art.gradle.external.constants.IMPLEMENTATION_CONFIGURATION_NAME
import io.art.gradle.external.constants.JAVA_MODULES
import io.art.gradle.external.constants.KOTLIN_MODULES
import io.art.gradle.external.plugin.externalPlugin
import org.gradle.api.artifacts.Configuration
import javax.inject.Inject

open class ModulesConfiguration @Inject constructor() {
    var javaModules = JAVA_MODULES.toMutableSet()
        private set

    var kotlinModules = KOTLIN_MODULES.toMutableSet()
        private set

    var version = MAIN.version
        private set

    var configuration: Configuration? = externalPlugin.project
            .configurations
            .findByName(IMPLEMENTATION_CONFIGURATION_NAME)

    fun excludeKotlin(vararg modules: String) {
        kotlinModules.removeAll(modules)
    }

    fun excludeJava(vararg modules: String) {
        javaModules.removeAll(modules)
    }

    fun includeKotlin(vararg modules: String) {
        kotlinModules.addAll(modules)
    }

    fun includeJava(vararg modules: String) {
        javaModules.addAll(modules)
    }

    fun version(version: String) {
        this.version = version
    }

    fun configuration(configuration: Configuration) {
        this.configuration = configuration
    }
}
