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

package io.art.gradle.external.configurator

import io.art.gradle.common.constants.MAVEN_REPOSITORY
import io.art.gradle.common.detector.hasJavaPlugin
import io.art.gradle.common.detector.hasKotlinPlugin
import io.art.gradle.external.constants.JAVA_GROUP
import io.art.gradle.external.constants.KOTLIN_GROUP
import io.art.gradle.external.plugin.externalPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.repositories

fun Project.configureModules() {
    with(externalPlugin.configuration.modules) {
        if (dependencies.isEmpty()) return

        repositories {
            maven {
                isAllowInsecureProtocol = true
                url = uri("https://135.181.2.177/repository/art-gradle-plugins/")
            }
        }

        dependencies {
            this@with.dependencies.asMap.forEach { dependency ->
                if (hasJavaPlugin) {
                    dependency.value.java.modules.forEach { module ->
                        add(dependency.key, "$JAVA_GROUP:${module.artifact}:$version")
                    }
                }

                if (hasKotlinPlugin) {
                    dependency.value.kotlin.modules.forEach { module ->
                        add(dependency.key, "$KOTLIN_GROUP:${module.artifact}:$version")
                    }
                }
            }
        }
    }
}

