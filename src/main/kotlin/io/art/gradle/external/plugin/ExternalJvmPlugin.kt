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

package io.art.gradle.external.plugin

import io.art.gradle.common.constants.ART
import io.art.gradle.external.configurator.addEmbeddedConfiguration
import io.art.gradle.external.configurator.configureExecutable
import io.art.gradle.external.configurator.configureGenerator
import io.art.gradle.external.configurator.configureModules
import io.art.gradle.external.extension.ExternalExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

lateinit var externalPlugin: ExternalJvmPlugin
    private set

class ExternalJvmPlugin : Plugin<Project> {
    lateinit var extension: ExternalExtension
        private set
    lateinit var project: Project
        private set

    override fun apply(target: Project) {
        externalPlugin = this
        project = target
        extension = target.extensions.create(ART)
        target.runCatching {
            addEmbeddedConfiguration()
            beforeEvaluate {
            }
            afterEvaluate {
                configureModules()
                configureExecutable()
                configureGenerator()
            }
        }.onFailure { error -> target.logger.error(error.message, error) }
    }
}


