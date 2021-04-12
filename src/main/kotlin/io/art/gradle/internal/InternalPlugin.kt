package io.art.gradle.internal

import org.gradle.api.Plugin
import org.gradle.api.Project

class InternalPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.run {
        configureRepositories()
        configurePublishing()
    }
}
