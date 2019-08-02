package ru.art.gradle.configurator.project

import org.gradle.api.*
import ru.art.gradle.configuration.*
import ru.art.gradle.context.Context.auxiliaryInformation
import ru.art.gradle.context.Context.projectConfiguration
import ru.art.gradle.context.Context.runAfterConfiguringAction
import ru.art.gradle.context.Context.setAfterConfiguringAction
import ru.art.gradle.determinator.*
import ru.art.gradle.logging.*

fun Project.configureProject() {
    applyJavaPlugin()
    applyIdeaPlugin()
    rootProject.applyIdeaPlugin()
    applyMavenPlugin()
    applyJavaLibraryPlugin()
    addDependencyConfigurations()
    setBranchByProperty()
    val projectConfiguration = projectConfiguration()
    afterEvaluate {
        attention("Configure ART project after evaluate")
        configureRefreshing()
        calculateVersion()
        addRepositoryFromProperties()
        configurePublishing()
        configureTests()
        if (projectConfiguration.generatorConfiguration.packageName.isNotEmpty()) {
            addGeneratorDependency()
            auxiliaryInformation().hasGenerator = true
        }
        addModules()
        substituteDependencies()
        if (projectConfiguration.withLombok) {
            addLombokDependency()
            auxiliaryInformation().hasLombok = true
        }
        if (projectConfiguration.withProtobufGenerator) {
            applyProtobufPlugin()
            configureProtobufGenerator()
            auxiliaryInformation().hasProtobufGenerator = true
        }
        if (projectConfiguration.withSpock == true) {
            applyGroovyPlugin()
            addSpockDependency()
            auxiliaryInformation().hasSpock = true
        }
        if (projectConfiguration.withCheckstyle == true) {
            applyCheckStylePlugin()
            addCheckstyle()
            auxiliaryInformation().hasCheckstyle = true
        }
        if (projectConfiguration.withGatling == true) {
            applyGatlingPlugin()
            applyScalaPlugin()
            configureGatling()
            auxiliaryInformation().hasGatling = true
        }
        if (projectConfiguration.withJmh == true) {
            applyJmhPlugin()
            configureJmh()
            auxiliaryInformation().hasJmh = true
        }
        if (projectConfiguration.withKotlin == true) {
            applyKotlinPlugin()
            addKotlinDependency()
            auxiliaryInformation().hasKotlin = true
        }
        if (projectConfiguration.withScala == true) {
            applyScalaPlugin()
            addScalaDependency()
            auxiliaryInformation().hasScala = true
        }
        if (projectConfiguration.withGroovy == true) {
            if (projectConfiguration.withSpock != true) {
                applyGroovyPlugin()
            }
            addGroovyDependency()
            auxiliaryInformation().hasGroovy = true
        }
        if (projectConfiguration.withWeb == true) {
            configureWeb()
            auxiliaryInformation().hasWeb = true
        }
        with(determineSourceSets()) {
            if (hasGatling && projectConfiguration.withGatling == null) {
                applyGatlingPlugin()
                applyScalaPlugin()
                configureGatling()
                auxiliaryInformation().hasGatling = hasGatling
            }
            if (hasJmh && projectConfiguration.withJmh == null) {
                applyJmhPlugin()
                configureJmh()
                auxiliaryInformation().hasJmh = hasJmh
            }
            if (hasGroovy && projectConfiguration.withGroovy == null) {
                if (projectConfiguration.withSpock != true) {
                    applyGroovyPlugin()
                }
                addGroovyDependency()
                auxiliaryInformation().hasGroovy = hasGroovy
            }
            if (hasGroovyTests && projectConfiguration.withGroovy == null) {
                if (projectConfiguration.withSpock != true) {
                    applyGroovyPlugin()
                }
                addGroovyTestsDependency()
                auxiliaryInformation().hasGroovyTests = hasGroovyTests
            }
            if (hasScala && projectConfiguration.withScala == null) {
                applyScalaPlugin()
                addScalaDependency()
                auxiliaryInformation().hasScala = hasScala
            }
            if (hasScalaTests && projectConfiguration.withScala == null) {
                applyScalaPlugin()
                addScalaTestsDependency()
                auxiliaryInformation().hasScalaTests = hasScalaTests
            }
            if (hasKotlin && projectConfiguration.withKotlin == null) {
                applyKotlinPlugin()
                addKotlinDependency()
                auxiliaryInformation().hasKotlin = hasKotlin
            }
            if (hasKotlinTests && projectConfiguration.withKotlin == null) {
                applyKotlinPlugin()
                addKotlinTestsDependency()
                auxiliaryInformation().hasKotlinTests = hasKotlinTests
            }
            if (hasWeb && projectConfiguration.withWeb == null) {
                configureWeb()
                auxiliaryInformation().hasWeb = true
            }
        }
        calculateDependencyVersions()
        configureJava()
        configureIdea()
        logResultingConfiguration()
        if (projectConfiguration().useProGuard) {
            addProGuardTask()
        }

        attention("End of ART project configuring")
        runAfterConfiguringAction()
    }
}

fun Project.afterConfiguring(action: (configuration: ProjectConfiguration) -> Unit) = setAfterConfiguringAction(action)