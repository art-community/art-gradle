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

import io.art.gradle.common.configuration.ExecutableConfiguration
import io.art.gradle.common.configuration.NativeExecutableConfiguration
import io.art.gradle.common.constants.GRAAL_MUSL_OPTION
import io.art.gradle.common.constants.GRAAL_NETTY_STATIC_LINK_PROPERTY
import io.art.gradle.common.constants.GRAAL_STATIC_OPTION
import io.art.gradle.common.constants.STABLE_MAVEN_REPOSITORY
import io.art.gradle.external.configuration.ExternalConfiguration
import io.art.gradle.external.constants.JAVA_GROUP
import io.art.gradle.external.constants.JavaModules.TRANSPORT
import io.art.gradle.external.constants.KOTLIN_GROUP
import io.art.gradle.external.constants.KOTLIN_JVM_PLUGIN_ID
import io.art.gradle.external.constants.KotlinModules
import io.art.gradle.external.plugin.externalPlugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPlatformPlugin
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.repositories

fun Project.configureModules() {
    with(externalPlugin.configuration.modules) {
        if (dependencies.isEmpty()) return

        repositories {
            maven {
                url = uri(STABLE_MAVEN_REPOSITORY)
            }
        }

        dependencies {
            this@with.dependencies.asMap.forEach { dependency ->
                if (plugins.hasPlugin(JavaBasePlugin::class.java) || plugins.hasPlugin(JavaLibraryPlugin::class.java) || plugins.hasPlugin(JavaPlatformPlugin::class.java)) {
                    dependency.value.java.modules.forEach { module ->
                        add(dependency.key, "$JAVA_GROUP:${module.artifact}:$version")
                        if (module == TRANSPORT) {
                            addNettyGraalOption()
                        }
                    }
                }

                if (plugins.hasPlugin(KOTLIN_JVM_PLUGIN_ID)) {
                    dependency.value.kotlin.modules.forEach { module ->
                        add(dependency.key, "$KOTLIN_GROUP:${module.artifact}:$version")
                        if (module == KotlinModules.TRANSPORT) {
                            addNettyGraalOption()
                        }
                    }
                }
            }
        }
    }
}

private fun Project.addNettyGraalOption() {
    val native = findNativeExtension() ?: return
    if (!native.graalOptions.contains(GRAAL_STATIC_OPTION) || !native.graalOptions.contains(GRAAL_MUSL_OPTION)) {
        return
    }
    native.addGraalSystemPropery(GRAAL_NETTY_STATIC_LINK_PROPERTY, true.toString())
}

private fun Project.findNativeExtension(): NativeExecutableConfiguration? {
    val internal = extensions.findByType<ExecutableConfiguration>()?.native
    val external = extensions.findByType<ExternalConfiguration>()?.executable?.native
    return internal ?: external
}
