package ru.art.gradle.configurator.project

import org.gradle.api.*
import ru.art.gradle.context.Context.projectConfiguration
import ru.art.gradle.logging.*
import java.util.concurrent.TimeUnit.*

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