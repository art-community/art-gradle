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

package io.art.gradle.external.configurator

import io.art.gradle.external.constants.IMPLEMENTATION_CONFIGURATION_NAME
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.getValue

fun Project.configureExecutable() {
    configureJar()
    configureNative()
}

fun Project.addEmbeddedConfiguration() {
    val embedded: Configuration by configurations.creating {
        configurations.findByName(IMPLEMENTATION_CONFIGURATION_NAME)?.extendsFrom(this)
    }
}
