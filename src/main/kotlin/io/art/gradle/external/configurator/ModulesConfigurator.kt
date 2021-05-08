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
import io.art.gradle.external.constants.GRAAL_JAVA_MODULE
import io.art.gradle.external.constants.JAVA_GROUP
import io.art.gradle.external.constants.KOTLIN_GROUP
import io.art.gradle.external.constants.KOTLIN_JVM_PLUGIN
import io.art.gradle.external.plugin.externalPlugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.repositories

fun Project.configureModules() {
    with(externalPlugin.extension.modules) {
        configuration ?: return

        repositories {
            maven {
                url = uri(STABLE_MAVEN_REPOSITORY)
            }
        }

        dependencies {
            if (plugins.hasPlugin(JavaBasePlugin::class.java) || plugins.hasPlugin(JavaLibraryPlugin::class.java)) {
                javaModules.forEach { module ->
                    add(configuration!!.name, "$JAVA_GROUP:$module:$version")
                }
            }

            if (plugins.hasPlugin(KOTLIN_JVM_PLUGIN)) {
                kotlinModules.forEach { module ->
                    add(configuration!!.name, "$KOTLIN_GROUP:$module:$version")
                }
            }
            externalPlugin.extension.executable.mainClass ?: return@dependencies

            if (externalPlugin.extension.executable.nativeEnabled) {
                add(configuration!!.name, "$JAVA_GROUP:$GRAAL_JAVA_MODULE:$version")
            }
        }
    }
}
