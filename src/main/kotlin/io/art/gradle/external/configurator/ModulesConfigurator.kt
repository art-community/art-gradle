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

import io.art.gradle.common.constants.STABLE_MAVEN_REPOSITORY
import io.art.gradle.external.constants.*
import io.art.gradle.external.plugin.externalPlugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.repositories

fun Project.configureModules() {
    with(externalPlugin.extension.modules) {
        if (modules.isEmpty()) return

        repositories {
            maven {
                url = uri(STABLE_MAVEN_REPOSITORY)
            }
        }

        dependencies {
            this@with.modules.asMap.forEach { module ->
                if (plugins.hasPlugin(JavaBasePlugin::class.java) || plugins.hasPlugin(JavaLibraryPlugin::class.java)) {
                    module.value.java.modules.forEach { name ->
                        add(module.key, "$JAVA_GROUP:${name}:$version")
                    }
                }
                if (plugins.hasPlugin(KOTLIN_JVM_PLUGIN_ID)) {
                    module.value.kotlin.modules.forEach { name ->
                        add(module.key, "$KOTLIN_GROUP:${name}:$version")
                    }
                }
            }
            if (externalPlugin.extension.executable.nativeEnabled) {
                add(EMBEDDED_CONFIGURATION_NAME, "$JAVA_GROUP:$GRAAL_JAVA_MODULE:$version")
            }
        }
    }
}
