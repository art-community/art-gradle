/*
 * Copyright 2019 ART
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
import ru.art.gradle.context.Context.auxiliaryInformation
import ru.art.gradle.context.Context.projectConfiguration
import ru.art.gradle.context.Context.runAfterConfiguringAction
import ru.art.gradle.determinator.*
import ru.art.gradle.logging.*

fun Project.configureProject() {
    applyJavaPlugin()
    applyJavaLibraryPlugin()
    addDependencyConfigurations()
    afterEvaluate {
        attention("Configure ART project after evaluate")
        val projectConfiguration = projectConfiguration()
        if (projectConfiguration.ideaConfiguration.enabled == true) {
            applyIdeaPlugin()
            rootProject.applyIdeaPlugin()
        }
        if (projectConfiguration.dependencyRefreshingConfiguration.enabled == true) {
            configureRefreshing()
        }
        if (projectConfiguration.testsConfiguration.enabled == true) {
            configureTests()
        }
        if (projectConfiguration.generatorConfiguration.packageName.isNotEmpty()) {
            addGeneratorDependency()
            auxiliaryInformation().hasGenerator = true
        }
        if (projectConfiguration.lombokConfiguration.enabled == true) {
            addLombokDependency()
            auxiliaryInformation().hasLombok = true
        }
        if (projectConfiguration.protobufGeneratorConfiguration.enabled == true) {
            applyProtobufPlugin()
            configureProtobufGenerator()
            auxiliaryInformation().hasProtobufGenerator = true
        }
        if (projectConfiguration.spockFrameworkConfiguration.enabled == true) {
            applyGroovyPlugin()
            addGroovyTestsDependency()
            addSpockDependency()
            auxiliaryInformation().hasSpock = true
        }
        if (projectConfiguration.checkstyleConfiguration.enabled == true) {
            applyCheckStylePlugin()
            addCheckstyle()
            auxiliaryInformation().hasCheckstyle = true
        }
        if (projectConfiguration.gatlingConfiguration.enabled == true) {
            applyGatlingPlugin()
            applyScalaPlugin()
            configureGatling()
            auxiliaryInformation().hasGatling = true
        }
        if (projectConfiguration.jmhConfiguration.enabled == true) {
            applyJmhPlugin()
            configureJmh()
            auxiliaryInformation().hasJmh = true
        }
        if (projectConfiguration.kotlinConfiguration.enabled == true) {
            applyKotlinPlugin()
            addKotlinDependency()
            auxiliaryInformation().hasKotlin = true
        }
        if (projectConfiguration.scalaConfiguration.enabled == true) {
            applyScalaPlugin()
            addScalaDependency()
            auxiliaryInformation().hasScala = true
        }
        if (projectConfiguration.groovyConfiguration.enabled == true) {
            if (projectConfiguration.spockFrameworkConfiguration.enabled != true) {
                applyGroovyPlugin()
            }
            addGroovyDependency()
            auxiliaryInformation().hasGroovy = true
        }
        if (projectConfiguration.webConfiguration.enabled == true) {
            configureWeb()
            auxiliaryInformation().hasWeb = true
        }
        with(determineSourceSets()) {
            if (hasGatling && projectConfiguration.gatlingConfiguration.enabled == null) {
                applyGatlingPlugin()
                applyScalaPlugin()
                configureGatling()
                auxiliaryInformation().hasGatling = hasGatling
            }
            if (hasJmh && projectConfiguration.jmhConfiguration.enabled == null) {
                applyJmhPlugin()
                configureJmh()
                auxiliaryInformation().hasJmh = hasJmh
            }
            if (hasGroovy && projectConfiguration.groovyConfiguration.enabled == null) {
                if (projectConfiguration.spockFrameworkConfiguration.enabled != true) {
                    applyGroovyPlugin()
                }
                addGroovyDependency()
                auxiliaryInformation().hasGroovy = hasGroovy
            }
            if (hasGroovyTests && projectConfiguration.groovyConfiguration.enabled == null) {
                if (projectConfiguration.spockFrameworkConfiguration.enabled != true) {
                    applyGroovyPlugin()
                    addGroovyTestsDependency()
                }
                auxiliaryInformation().hasGroovyTests = hasGroovyTests
            }
            if (hasScala && projectConfiguration.scalaConfiguration.enabled == null) {
                applyScalaPlugin()
                addScalaDependency()
                auxiliaryInformation().hasScala = hasScala
            }
            if (hasScalaTests && projectConfiguration.scalaConfiguration.enabled == null) {
                applyScalaPlugin()
                addScalaTestsDependency()
                auxiliaryInformation().hasScalaTests = hasScalaTests
            }
            if (hasKotlin && projectConfiguration.kotlinConfiguration.enabled == null) {
                applyKotlinPlugin()
                addKotlinDependency()
                auxiliaryInformation().hasKotlin = hasKotlin
            }
            if (hasKotlinTests && projectConfiguration.kotlinConfiguration.enabled == null) {
                applyKotlinPlugin()
                addKotlinTestsDependency()
                auxiliaryInformation().hasKotlinTests = hasKotlinTests
            }
            if (hasWeb && projectConfiguration.webConfiguration.enabled == null) {
                configureWeb()
                auxiliaryInformation().hasWeb = true
            }
        }
        calculateVersion()
        addModules()
        substituteDependencies()
        calculateDependencyVersions()
        configureJava()
        if (projectConfiguration.ideaConfiguration.enabled == true) {
            configureIdea()
        }
        logResultingConfiguration()
        runAfterConfiguringAction()
        attention("End of ART project configuring")
    }
}