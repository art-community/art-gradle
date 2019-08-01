package ru.adk.gradle.plugin.configurator.project

import org.gradle.api.Project
import ru.adk.gradle.plugin.context.Context.projectConfiguration
import ru.adk.gradle.plugin.logging.additionalAttention
import ru.adk.gradle.plugin.logging.success
import java.util.concurrent.TimeUnit.SECONDS

fun Project.configureRefreshing() {
    configurations.all { configuration ->
        configuration.resolutionStrategy { strategy ->
            strategy.cacheChangingModulesFor(projectConfiguration().dependencyRefreshingConfiguration.refreshingRateInSeconds, SECONDS)
        }
    }

    dependencies.components.all { details ->
        details.isChanging = true
    }

    success("For changing dependencies (all configurations) set refreshing time to ${projectConfiguration().dependencyRefreshingConfiguration.refreshingRateInSeconds}[s]")
    additionalAttention("Set 'isChanging=true' to all dependencies(all configurations)")
}