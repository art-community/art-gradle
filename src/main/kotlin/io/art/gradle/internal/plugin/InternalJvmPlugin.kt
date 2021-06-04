package io.art.gradle.internal.plugin

import io.art.gradle.common.configuration.ExecutableConfiguration
import io.art.gradle.common.configurator.*
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
        executable = target.extensions.create(EXECUTABLE, target)
        generator = target.extensions.create(GENERATOR, target)
        project = target
        target.runCatching {
            addEmbeddedConfiguration()
            configureRepositories()
            configurePublishing()
            afterEvaluate {
                configureEmbeddedConfiguration()
                configureJar(executable)
                configureNative(executable)
                configureGenerator(generator)
            }
        }.onFailure(target::error)
    }
}
