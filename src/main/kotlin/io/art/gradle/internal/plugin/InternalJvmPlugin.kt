package io.art.gradle.internal.plugin

import io.art.gradle.internal.configurator.configurePublishing
import io.art.gradle.internal.configurator.configureRepositories
import io.art.gradle.common.logger.error
import org.gradle.api.Plugin
import org.gradle.api.Project

class InternalJvmPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.runCatching {
            configureRepositories()
            configurePublishing()
        }.onFailure(target::error)
    }
}
