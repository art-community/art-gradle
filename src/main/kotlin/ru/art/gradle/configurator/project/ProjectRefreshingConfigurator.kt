/*
 *    Copyright 2019 ART 
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

package ru.art.gradle.configurator.project

import org.gradle.api.*
import ru.art.gradle.context.Context.projectExtension
import ru.art.gradle.logging.*
import java.util.concurrent.TimeUnit.*

fun Project.configureRefreshing() {
    configurations.all { configuration ->
        configuration.resolutionStrategy { strategy ->
            strategy.cacheChangingModulesFor(projectExtension().dependencyRefreshingConfiguration.refreshingRateInSeconds, SECONDS)
        }
    }

    dependencies.components.all { details ->
        details.isChanging = true
    }

    success("For changing dependencies (all configurations) set refreshing time to ${projectExtension().dependencyRefreshingConfiguration.refreshingRateInSeconds}[s]")
    additionalAttention("Set 'isChanging=true' to all dependencies(all configurations)")
}