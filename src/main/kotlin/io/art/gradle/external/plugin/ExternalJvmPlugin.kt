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

import io.art.gradle.common.configurator.*
import io.art.gradle.common.constants.ART
import io.art.gradle.external.configuration.ExternalConfiguration
import io.art.gradle.external.configurator.configureModules
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

lateinit var externalPlugin: ExternalJvmPlugin
    private set

class ExternalJvmPlugin : Plugin<Project> {
    lateinit var configuration: ExternalConfiguration
        private set
    lateinit var project: Project
        private set

    override fun apply(target: Project) {
        externalPlugin = this
        project = target
        target.runCatching {
            configuration = target.extensions.create(ART)
            addEmbeddedConfiguration()
            addTestEmbeddedConfiguration()
            afterEvaluate {
                configureEmbeddedConfiguration()
                configureTestEmbeddedConfiguration()
                configureModules()
                configureExecutable(configuration.executable)
                configureTest(configuration.test)
            }
            gradle.projectsEvaluated { configureGenerator(configuration.generator) }
        }.onFailure { error -> target.logger.error(error.message, error) }
    }
}
