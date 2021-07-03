package io.art.gradle.internal.plugin

import io.art.gradle.common.configuration.ExecutableConfiguration
import io.art.gradle.common.configurator.addEmbeddedConfiguration
import io.art.gradle.common.configurator.configureEmbeddedConfiguration
import io.art.gradle.common.configurator.configureExecutable
import io.art.gradle.common.configurator.configureGenerator
import io.art.gradle.common.constants.EXECUTABLE
import io.art.gradle.common.constants.GENERATOR
import io.art.gradle.common.logger.error
import io.art.gradle.internal.configuration.InternalGeneratorConfiguration
import io.art.gradle.internal.configurator.configurePublishing
import io.art.gradle.internal.configurator.configureRepositories
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

lateinit var jvmPlugin: InternalJvmPlugin
    private set

class InternalJvmPlugin : Plugin<Project> {
    lateinit var executable: ExecutableConfiguration
        private set

    lateinit var generator: InternalGeneratorConfiguration
        private set

    lateinit var project: Project
        private set

    override fun apply(target: Project) {
        jvmPlugin = this
        project = target
        target.runCatching {
            executable = target.extensions.create(EXECUTABLE, target)
            generator = target.extensions.create(GENERATOR, target)
            addEmbeddedConfiguration()
            configureRepositories()
            configurePublishing()
            afterEvaluate {
                configureEmbeddedConfiguration()
                configureExecutable(executable)
            }
            gradle.projectsEvaluated { configureGenerator(generator) }
        }.onFailure(target::error)
    }
}
