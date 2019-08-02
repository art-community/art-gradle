/*
 * Copyright 2019 ART
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.art.gradle.configurator.settings

import org.gradle.api.initialization.*
import ru.art.gradle.constants.*
import ru.art.gradle.context.Context.settingsConfiguration
import java.net.*

fun Settings.configurePluginManagement() {
    pluginManagement { management ->
        with(management) {
            resolutionStrategy { strategy ->
                strategy.eachPlugin { plugin ->
                    with(plugin) {
                        if (requested.id.id == ART_PROJECT) {
                            useModule("$ART_PLUGIN_DEPENDENCY:${requested.version}")
                        }
                        if (requested.id.id == ART_SETTINGS) {
                            useModule("$ART_PLUGIN_DEPENDENCY:${requested.version}")
                        }
                    }
                }
            }
            repositories { handler ->
                handler.maven { maven ->
                    with(maven) {
                        with(settingsConfiguration.repositoryConfiguration) {
                            val repositoryId = repositoryId
                            url = URL("${getUrlParameter(this@configurePluginManagement)}/$repositoryId").toURI()
                            credentials { credentials ->
                                credentials.username = getUsernameParameter(this@configurePluginManagement)
                                credentials.password = getPasswordParameter(this@configurePluginManagement)
                            }
                        }
                    }
                }
            }
        }
    }
}