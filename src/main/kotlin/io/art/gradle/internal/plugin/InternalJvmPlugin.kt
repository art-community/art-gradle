package io.art.gradle.internal.plugin

import SourceDependenciesConfiguration
import io.art.gradle.common.configuration.ExecutableConfiguration
import io.art.gradle.common.configuration.TestConfiguration
import io.art.gradle.common.configurator.*
import io.art.gradle.common.constants.EXECUTABLE
import io.art.gradle.common.constants.GENERATOR
import io.art.gradle.common.constants.SOURCES
import io.art.gradle.common.constants.TEST_EXECUTION
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

    lateinit var test: TestConfiguration
        private set

    lateinit var generator: InternalGeneratorConfiguration
        private set

    lateinit var sources: SourceDependenciesConfiguration
        private set

    lateinit var project: Project
        private set

    override fun apply(target: Project) {
        jvmPlugin = this
        project = target
        target.runCatching {
            executable = target.extensions.create(EXECUTABLE, target)
            test = target.extensions.create(TEST_EXECUTION, target)
            generator = target.extensions.create(GENERATOR, target)
            sources = target.extensions.create(SOURCES, target)
            addEmbeddedConfiguration()
            addTestEmbeddedConfiguration()
            configureRepositories()
            configurePublishing()
            afterEvaluate {
                configureEmbeddedConfiguration()
                configureTestEmbeddedConfiguration()
            }
            gradle.projectsEvaluated {
                configureGenerator(generator)
                configureExecutable(executable)
                configureTest(test)
                configureSourceDependencies(sources)
            }
        }.onFailure(target::error)
    }
}
