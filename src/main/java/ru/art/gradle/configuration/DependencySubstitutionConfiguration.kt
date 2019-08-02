package ru.art.gradle.configuration

import org.gradle.api.*
import org.gradle.api.artifacts.*
import ru.art.gradle.context.Context.projectConfiguration
import ru.art.gradle.dependency.Dependency
import javax.inject.*

open class DependencySubstitutionConfiguration @Inject constructor(val project: Project) {
    var codeSubstitutions: MutableSet<Dependency> = mutableSetOf()
        private set
    var artifactSubstitutions: MutableSet<Dependency> = mutableSetOf()
        private set

    fun substituteWithCode(group: String, artifact: String) {
        substituteWithCode(Dependency(group = group, artifact = artifact))
    }

    fun substituteWithArtifact(group: String, artifact: String) {
        substituteWithArtifact(Dependency(group = group, artifact = artifact))
    }

    fun substituteWithCode(dependency: Dependency) {
        project.findProject(":${dependency.artifact}") ?: return
        codeSubstitutions.add(dependency)
        artifactSubstitutions.remove(dependency)
    }

    fun substituteWithArtifact(dependency: Dependency) {
        codeSubstitutions.remove(dependency)
        artifactSubstitutions.add(dependency)
    }
}

fun Project.substituteWithCode(dependency: ExternalModuleDependency) {
    projectConfiguration()
            .dependencySubstitutionConfiguration
            .substituteWithCode(dependency.group ?: return, dependency.name)
}

fun Project.substituteWithArtifact(dependency: ExternalModuleDependency) {
    projectConfiguration()
            .dependencySubstitutionConfiguration
            .substituteWithArtifact(dependency.group ?: return, dependency.name)
}

fun Project.substituteWithCode(dependency: Dependency) = projectConfiguration().dependencySubstitutionConfiguration.substituteWithCode(dependency.group, dependency.artifact)

fun Project.substituteWithArtifact(dependency: Dependency) = projectConfiguration().dependencySubstitutionConfiguration.substituteWithArtifact(dependency.group, dependency.artifact)