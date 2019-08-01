package ru.adk.gradle.plugin.configurator.project

import org.gradle.api.*
import org.gradle.api.artifacts.*
import org.gradle.kotlin.dsl.*
import ru.adk.gradle.plugin.constants.*
import ru.adk.gradle.plugin.constants.DependencyConfiguration.*
import ru.adk.gradle.plugin.dependency.Dependency
import ru.adk.gradle.plugin.logging.*

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
    addDependency(COMPILE_CLASSPATH, lombok())
    addDependency(TEST_COMPILE_CLASSPATH, lombok())
    addDependency(TEST_IMPLEMENTATION, lombok())
    addDependency(TEST_RUNTIME_CLASSPATH, lombok())
}

fun Project.addSpockDependency() {
    addDependency(TEST_COMPILE_CLASSPATH, spock())
    addDependency(TEST_COMPILE_CLASSPATH, cglib())
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
}

fun Project.addKotlinTestsDependency() {
    addDependency(TEST_COMPILE_CLASSPATH, kotlin())
}

fun Project.addScalaTestsDependency() {
    addDependency(TEST_COMPILE_CLASSPATH, scala())
}

fun Project.addGeneratorDependency() {
    addDependency(PROVIDED, generator())
}