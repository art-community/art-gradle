/*
 *    Copyright 2019 ART 
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.art.gradle.configurator.project

import org.gradle.api.*
import org.gradle.api.artifacts.*
import org.gradle.kotlin.dsl.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.DependencyConfiguration.*
import ru.art.gradle.dependency.Dependency
import ru.art.gradle.logging.*

fun Project.addDependency(configuration: DependencyConfiguration, dependency: Dependency) = dependencies {
    val moduleDependency = configuration.configuration(dependency.inGradleNotation()) as ModuleDependency
    dependency.exclusions.forEach { exclude -> moduleDependency.exclude(exclude.group, exclude.artifact) }
    val exclusionMessage = if (dependency.exclusions.isEmpty()) EMPTY_STRING else "and exclusions: ${dependency.exclusions.joinToString { exclude -> exclude.inGradleNotation() }}"
    success("Adding dependency(${dependency.inGradleNotation()}) with '${configuration.configuration}' configuration $exclusionMessage")
}

fun Project.addLombokDependency() {
    if (gradle.gradleVersion.startsWith(GRADLE_VERSION_5)) {
        addDependency(ANNOTATION_PROCESSOR, lombok())
    }
    addDependency(COMPILE_ONLY, lombok())
    addDependency(TEST_COMPILE_ONLY, lombok())
}

fun Project.addSpockDependency() {
    addDependency(TEST_COMPILE_CLASSPATH, spock())
    addDependency(TEST_RUNTIME_CLASSPATH, spock())
    addDependency(TEST_COMPILE_CLASSPATH, cglib())
    addDependency(TEST_RUNTIME_CLASSPATH, cglib())
}

fun Project.addGroovyDependency() {
    addDependency(EMBEDDED, groovy())
}

fun Project.addKotlinDependency() {
    addDependency(EMBEDDED, kotlin())
}

fun Project.addScalaDependency() {
    addDependency(EMBEDDED, scala())
}

fun Project.addGroovyTestsDependency() {
    addDependency(TEST_COMPILE_CLASSPATH, groovy())
    addDependency(TEST_RUNTIME_CLASSPATH, groovy())
}

fun Project.addKotlinTestsDependency() {
    addDependency(TEST_COMPILE_CLASSPATH, kotlin())
    addDependency(TEST_RUNTIME_CLASSPATH, kotlin())
}

fun Project.addScalaTestsDependency() {
    addDependency(TEST_COMPILE_CLASSPATH, scala())
    addDependency(TEST_RUNTIME_CLASSPATH, scala())
}

fun Project.addGeneratorDependency() {
    addDependency(PROVIDED, generator())
}