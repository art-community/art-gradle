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
import ru.art.gradle.context.Context.auxiliaryInformation
import ru.art.gradle.context.Context.projectExtension
import ru.art.gradle.context.Context.runAfterConfiguringAction
import ru.art.gradle.determinator.*
import ru.art.gradle.logging.*

fun Project.configureProject() {
    applyJavaPlugin()
    applyJavaLibraryPlugin()
    addDependencyConfigurations()
    afterEvaluate {
        attention("Configure ART project after evaluate")
        val projectExtension = projectExtension()
        if (projectExtension.ideaConfiguration.enabled == true) {
            applyIdeaPlugin()
            rootProject.applyIdeaPlugin()
        }
        if (projectExtension.dependencyRefreshingConfiguration.enabled == true) {
            configureRefreshing()
        }
        if (projectExtension.testsConfiguration.enabled == true) {
            configureTests()
        }
        if (projectExtension.generatorConfiguration.packageName.isNotEmpty()) {
            addGeneratorDependency()
            configureGenerator()
            auxiliaryInformation().hasGenerator = true
        }
        if (projectExtension.lombokConfiguration.enabled == true) {
            addLombokDependency()
            auxiliaryInformation().hasLombok = true
        }
        if (projectExtension.protobufGeneratorConfiguration.enabled == true) {
            applyProtobufPlugin()
            configureProtobufGenerator()
            auxiliaryInformation().hasProtobufGenerator = true
        }
        if (projectExtension.spockFrameworkConfiguration.enabled == true) {
            applyGroovyPlugin()
            addGroovyTestsDependency()
            addSpockDependency()
            auxiliaryInformation().hasSpock = true
            auxiliaryInformation().hasGroovyTests = true
        }
        if (projectExtension.checkstyleConfiguration.enabled == true) {
            applyCheckStylePlugin()
            addCheckstyle()
            auxiliaryInformation().hasCheckstyle = true
        }
        if (projectExtension.gatlingConfiguration.enabled == true) {
            applyGatlingPlugin()
            applyScalaPlugin()
            configureGatling()
            auxiliaryInformation().hasGatling = true
        }
        if (projectExtension.jmhConfiguration.enabled == true) {
            applyJmhPlugin()
            configureJmh()
            auxiliaryInformation().hasJmh = true
        }
        if (projectExtension.kotlinConfiguration.enabled == true) {
            applyKotlinPlugin()
            addKotlinDependency()
            auxiliaryInformation().hasKotlin = true
        }
        if (projectExtension.scalaConfiguration.enabled == true) {
            applyScalaPlugin()
            addScalaDependency()
            auxiliaryInformation().hasScala = true
        }
        if (projectExtension.groovyConfiguration.enabled == true) {
            if (projectExtension.spockFrameworkConfiguration.enabled != true) {
                applyGroovyPlugin()
            }
            addGroovyDependency()
            auxiliaryInformation().hasGroovy = true
        }
        if (projectExtension.webConfiguration.enabled == true) {
            configureWeb()
            auxiliaryInformation().hasWeb = true
        }
        with(determineSourceSets()) {
            if (hasGatling && projectExtension.gatlingConfiguration.enabled == null) {
                applyGatlingPlugin()
                applyScalaPlugin()
                configureGatling()
                auxiliaryInformation().hasGatling = hasGatling
            }
            if (hasJmh && projectExtension.jmhConfiguration.enabled == null) {
                applyJmhPlugin()
                configureJmh()
                auxiliaryInformation().hasJmh = hasJmh
            }
            if (hasGroovy && projectExtension.groovyConfiguration.enabled == null) {
                if (projectExtension.spockFrameworkConfiguration.enabled != true) {
                    applyGroovyPlugin()
                }
                addGroovyDependency()
                auxiliaryInformation().hasGroovy = hasGroovy
            }
            if (hasGroovyTests && projectExtension.groovyConfiguration.enabled == null) {
                if (projectExtension.spockFrameworkConfiguration.enabled != true) {
                    applyGroovyPlugin()
                    addGroovyTestsDependency()
                }
                auxiliaryInformation().hasGroovyTests = hasGroovyTests
            }
            if (hasScala && projectExtension.scalaConfiguration.enabled == null) {
                applyScalaPlugin()
                addScalaDependency()
                auxiliaryInformation().hasScala = hasScala
            }
            if (hasScalaTests && projectExtension.scalaConfiguration.enabled == null) {
                applyScalaPlugin()
                addScalaTestsDependency()
                auxiliaryInformation().hasScalaTests = hasScalaTests
            }
            if (hasKotlin && projectExtension.kotlinConfiguration.enabled == null) {
                applyKotlinPlugin()
                addKotlinDependency()
                auxiliaryInformation().hasKotlin = hasKotlin
            }
            if (hasKotlinTests && projectExtension.kotlinConfiguration.enabled == null) {
                applyKotlinPlugin()
                addKotlinTestsDependency()
                auxiliaryInformation().hasKotlinTests = hasKotlinTests
            }
            if (hasWeb && projectExtension.webConfiguration.enabled == null) {
                configureWeb()
                auxiliaryInformation().hasWeb = true
            }
        }
        calculateVersion()
        addModules()
        substituteDependencies()
        configureJava()
        if (projectExtension.ideaConfiguration.enabled == true) {
            configureIdea()
        }
        logResultingConfiguration()
        runAfterConfiguringAction()
        attention("End of ART project configuring")
    }
}