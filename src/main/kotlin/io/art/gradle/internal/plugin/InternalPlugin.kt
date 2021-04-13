package io.art.gradle.internal.plugin

import io.art.gradle.internal.configurator.configurePublishing
import io.art.gradle.internal.configurator.configureRepositories
import org.gradle.api.Plugin
import org.gradle.api.Project

class InternalPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.run {
        configureRepositories()
        configurePublishing()
    }
}
