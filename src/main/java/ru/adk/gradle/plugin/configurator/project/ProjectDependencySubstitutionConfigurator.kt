package ru.adk.gradle.plugin.configurator.project

import org.gradle.api.*
import org.gradle.api.internal.project.*
import ru.adk.gradle.plugin.constants.*
import ru.adk.gradle.plugin.constants.DefaultTasks.BUILD
import ru.adk.gradle.plugin.context.Context.projectConfiguration
import ru.adk.gradle.plugin.dependency.*
import ru.adk.gradle.plugin.logging.*

fun Project.substituteDependencies() {
    val substitutedDependencies = mutableSetOf<Dependency>()
    projectConfiguration().dependencySubstitutionConfiguration.codeSubstitutions
            .forEach { dependency ->
                configurations.all { configuration ->
                    findProject(":${dependency.artifact}") ?: return@all
                    substitutedDependencies.add(dependency)
                    val dependencyProject = project(":${dependency.artifact}") as DefaultProject
                    if (dependencyProject.state.isUnconfigured) {
                        dependencyProject.evaluate()
                    }
                    val module = configuration.resolutionStrategy.dependencySubstitution.module(dependency.inGradleNotation())
                    val project = configuration.resolutionStrategy.dependencySubstitution.project(":${dependency.artifact}")
                    configuration.resolutionStrategy.dependencySubstitution.substitute(module).with(project)
                    tasks.filter { task -> task.name.startsWith(COMPILE_TASK_PREFIX) }.forEach { it.dependsOn(":${dependency.artifact}:$BUILD") }
                }
            }
    substitutedDependencies.forEach { substitutedDependency ->
        success("Substitute dependency(${substitutedDependency.inGradleNotation()}) with project(:${substitutedDependency.artifact}) for all configurations")

    }
}