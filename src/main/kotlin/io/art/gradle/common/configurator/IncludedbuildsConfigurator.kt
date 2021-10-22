package io.art.gradle.common.configurator

import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ProjectComponentIdentifier


private fun Task.dependsOnIncludedBuilds(configuration: Configuration) {
    configuration.incoming.resolutionResult.allDependencies {
        if (from.id is ProjectComponentIdentifier) {
            val id = from.id as ProjectComponentIdentifier
            project.gradle.includedBuilds
                    .filter { build -> id.build.name == build.name }
                    .forEach { build -> dependsOn(build.task(":${id.projectName}:${name}")) }
        }
    }
}

private fun Task.dependsOnIncludedBuildsIncludeChildren(configuration: Configuration) {
    configuration.incoming.resolutionResult.allDependencies {
        if (from.id is ProjectComponentIdentifier) {
            val id = from.id as ProjectComponentIdentifier
            project.gradle.includedBuilds
                    .filter { build -> id.build.name == build.name }
                    .forEach { build -> dependsOn(build.task(":${id.projectName}:${name}")) }
            project.rootProject
                    .subprojects
                    .filter { subProject -> id.build.isCurrentBuild && subProject.name == id.projectName }
                    .forEach { subProject -> dependsOn(":${subProject.name}:${name}") }
        }
    }
}
