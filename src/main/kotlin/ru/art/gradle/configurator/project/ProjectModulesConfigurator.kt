/*
 * ART Java
 *
 * Copyright 2019 ART
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.art.gradle.configurator.project

import org.gradle.api.*
import ru.art.gradle.configuration.*
import ru.art.gradle.constants.DependencyConfiguration.*
import ru.art.gradle.context.Context.projectExtension
import ru.art.gradle.dependency.*

fun Project.addModules() {
    val projectExtension = projectExtension()
    val embeddedModulesConfiguration = projectExtension.embeddedModulesConfiguration
    val providedModulesConfiguration = projectExtension.providedModulesConfiguration
    val testModulesConfiguration = projectExtension.testModulesConfiguration

    val embeddedModules = embeddedModulesConfiguration.modules
    val providedModules = providedModulesConfiguration.modules.filter { !embeddedModules.contains(it) }
    val testModules = testModulesConfiguration.modules.filter { !providedModules.contains(it) }

    embeddedModules.stream()
            .peek(::substituteModuleWithCode)
            .peek { dependency -> setVersion(dependency, embeddedModulesConfiguration) }
            .forEach { addDependency(EMBEDDED, it) }
    providedModules.stream()
            .peek(::substituteModuleWithCode)
            .peek { dependency -> setVersion(dependency, providedModulesConfiguration) }
            .forEach { addDependency(PROVIDED, it) }
    testModules.stream()
            .peek(::substituteModuleWithCode)
            .peek { dependency -> setVersion(dependency, testModulesConfiguration) }
            .forEach {
                addDependency(TEST_COMPILE_CLASSPATH, it)
                addDependency(TEST_RUNTIME_CLASSPATH, it)
            }
}

fun Project.substituteModuleWithCode(module: Dependency) {
    if (projectExtension().dependencySubstitutionConfiguration.artifactSubstitutions.contains(module)) {
        return
    }
    if (parent?.subprojects?.any { project -> project.name == module.artifact } == true) {
        projectExtension().dependencySubstitutionConfiguration.substituteWithCode(module)
    }
}

fun Project.setVersion(module: Dependency, configuration: ModulesCombinationConfiguration) {
    if (projectExtension().dependencySubstitutionConfiguration.codeSubstitutions.contains(module)) {
        return
    }
    if (module.version.isNullOrBlank()) {
        module.version = configuration.version
    }
}