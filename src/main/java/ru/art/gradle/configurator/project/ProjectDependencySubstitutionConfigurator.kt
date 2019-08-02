package ru.art.gradle.configurator.project

import org.gradle.api.*
import org.gradle.api.internal.project.*
import ru.art.gradle.dependency.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.DefaultTasks.BUILD
import ru.art.gradle.context.Context.projectConfiguration
import ru.art.gradle.dependency.*
import ru.art.gradle.logging.*

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