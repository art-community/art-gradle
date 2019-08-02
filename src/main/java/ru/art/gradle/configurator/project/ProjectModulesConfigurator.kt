package ru.art.gradle.configurator.project

import org.gradle.api.*
import ru.art.gradle.dependency.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.DependencyConfiguration.*
import ru.art.gradle.context.Context.projectConfiguration
import ru.art.gradle.dependency.*

fun Project.addModules() {
    val projectConfiguration = projectConfiguration()
    val embeddedModulesConfiguration = projectConfiguration.embeddedModulesConfiguration
    val providedModulesConfiguration = projectConfiguration.providedModulesConfiguration
    val testModulesConfiguration = projectConfiguration.testModulesConfiguration

    val embeddedModules = embeddedModulesConfiguration.modules
    val providedModules = providedModulesConfiguration.modules.filter { !embeddedModules.contains(it) }
    val testModules = testModulesConfiguration.modules.filter { !providedModules.contains(it) }

    embeddedModules.stream()
            .peek(::substituteModuleWithCode)
            .map { module -> Dependency(ART_MODULE_GROUP, module.artifact) }
            .peek { dependency -> applyVersionSelectionMode(dependency, embeddedModulesConfiguration.versionSelectionMode, embeddedModulesConfiguration.version) }
            .forEach { addDependency(EMBEDDED, it) }
    providedModules.stream()
            .peek(::substituteModuleWithCode)
            .map { module -> Dependency(ART_MODULE_GROUP, module.artifact) }
            .peek { dependency -> applyVersionSelectionMode(dependency, providedModulesConfiguration.versionSelectionMode, providedModulesConfiguration.version) }
            .forEach { addDependency(PROVIDED, it) }
    testModules.stream()
            .peek(::substituteModuleWithCode)
            .map { module -> Dependency(ART_MODULE_GROUP, module.artifact) }
            .peek { dependency -> applyVersionSelectionMode(dependency, testModulesConfiguration.versionSelectionMode, testModulesConfiguration.version) }
            .forEach { addDependency(TEST_COMPILE_CLASSPATH, it) }
}

private fun Project.substituteModuleWithCode(module: Dependency) {
    if (projectConfiguration().dependencySubstitutionConfiguration.artifactSubstitutions.contains(module)) {
        return
    }
    if (parent?.subprojects?.any { project -> project.name == module.artifact } == true) {
        projectConfiguration().dependencySubstitutionConfiguration.substituteWithCode(module)
    }
}

private fun Project.applyVersionSelectionMode(module: Dependency, versionSelectionMode: DependencyVersionSelectionMode, version: Any?) {
    if (projectConfiguration().dependencySubstitutionConfiguration.codeSubstitutions.contains(module)) {
        return
    }
    if (projectConfiguration().dependencyVersionsConfiguration.versionSelectionModes.contains(module)) {
        return
    }
    projectConfiguration().dependencyVersionsConfiguration.addDependencyVersion(module, versionSelectionMode, version)
}