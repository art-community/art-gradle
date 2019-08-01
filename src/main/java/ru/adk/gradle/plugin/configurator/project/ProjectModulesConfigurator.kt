package ru.adk.gradle.plugin.configurator.project

import org.gradle.api.*
import ru.adk.gradle.plugin.constants.*
import ru.adk.gradle.plugin.constants.DependencyConfiguration.*
import ru.adk.gradle.plugin.context.Context.projectConfiguration
import ru.adk.gradle.plugin.dependency.*

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
            .map { module -> Dependency(ADK_MODULE_GROUP_SELECTOR(module.group, embeddedModulesConfiguration.versionSelectionMode, embeddedModulesConfiguration.version), module.artifact) }
            .peek { dependency -> applyVersionSelectionMode(dependency, embeddedModulesConfiguration.versionSelectionMode, embeddedModulesConfiguration.version) }
            .forEach { addDependency(EMBEDDED, it) }
    providedModules.stream()
            .peek(::substituteModuleWithCode)
            .map { module -> Dependency(ADK_MODULE_GROUP_SELECTOR(module.group, providedModulesConfiguration.versionSelectionMode, providedModulesConfiguration.version), module.artifact) }
            .peek { dependency -> applyVersionSelectionMode(dependency, providedModulesConfiguration.versionSelectionMode, providedModulesConfiguration.version) }
            .forEach { addDependency(PROVIDED, it) }
    testModules.stream()
            .peek(::substituteModuleWithCode)
            .map { module -> Dependency(ADK_MODULE_GROUP_SELECTOR(module.group, testModulesConfiguration.versionSelectionMode, testModulesConfiguration.version), module.artifact) }
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