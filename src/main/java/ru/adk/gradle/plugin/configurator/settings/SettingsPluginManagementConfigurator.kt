package ru.adk.gradle.plugin.configurator.settings

import org.gradle.api.initialization.*
import ru.adk.gradle.plugin.constants.*
import ru.adk.gradle.plugin.context.Context.settingsConfiguration
import java.net.*

fun Settings.configurePluginManagement() {
    pluginManagement { management ->
        with(management) {
            resolutionStrategy { strategy ->
                strategy.eachPlugin { plugin ->
                    with(plugin) {
                        if (requested.id.id == ADK_PROJECT) {
                            useModule("$ADK_PLUGIN_DEPENDENCY:${requested.version}")
                        }
                        if (requested.id.id == ADK_SETTINGS) {
                            useModule("$ADK_PLUGIN_DEPENDENCY:${requested.version}")
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