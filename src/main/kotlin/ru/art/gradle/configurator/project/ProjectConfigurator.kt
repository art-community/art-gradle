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
        with(projectExtension()) {
            if (ideaConfiguration.enabled == true) {
                applyIdeaPlugin()
                rootProject.applyIdeaPlugin()
            }
            if (dependencyRefreshingConfiguration.enabled == true) {
                configureRefreshing()
            }
            if (testsConfiguration.enabled == true) {
                configureTests()
            }
            with(generatorConfiguration) {
                if (packageName.isNotEmpty()) {
                    configureGenerator()
                    auxiliaryInformation().hasGenerator = true
                }
                if (enableSoap && (packageName.isNotBlank() || soapConfiguration.packageName.isNotBlank())) {
                    configureSoapGenerator()
                    auxiliaryInformation().hasSoapGenerator = true
                }
            }
            if (lombokConfiguration.enabled == true) {
                addLombokDependency()
                auxiliaryInformation().hasLombok = true
            }
            if (protobufGeneratorConfiguration.enabled == true) {
                applyProtobufPlugin()
                configureProtobufGenerator()
                auxiliaryInformation().hasProtobufGenerator = true
            }
            if (spockFrameworkConfiguration.enabled == true) {
                applyGroovyPlugin()
                addGroovyTestsDependency()
                addSpockDependency()
                auxiliaryInformation().hasSpock = true
                auxiliaryInformation().hasGroovyTests = true
            }
            if (checkstyleConfiguration.enabled == true) {
                applyCheckStylePlugin()
                addCheckstyle()
                auxiliaryInformation().hasCheckstyle = true
            }
            if (gatlingConfiguration.enabled == true) {
                applyScalaPlugin()
                configureGatling()
                auxiliaryInformation().hasGatling = true
            }
            if (jmhConfiguration.enabled == true) {
                applyJmhPlugin()
                configureJmh()
                auxiliaryInformation().hasJmh = true
            }
            if (kotlinConfiguration.enabled == true) {
                applyKotlinPlugin()
                addKotlinDependency()
                auxiliaryInformation().hasKotlin = true
            }
            if (scalaConfiguration.enabled == true) {
                applyScalaPlugin()
                addScalaDependency()
                auxiliaryInformation().hasScala = true
            }
            if (groovyConfiguration.enabled == true) {
                if (spockFrameworkConfiguration.enabled != true) {
                    applyGroovyPlugin()
                }
                addGroovyDependency()
                auxiliaryInformation().hasGroovy = true
            }
            if (webConfiguration.enabled == true) {
                configureWeb()
                auxiliaryInformation().hasWeb = true
            }
            with(determineSourceSets()) {
                if (hasGatling && gatlingConfiguration.enabled == null) {
                    applyScalaPlugin()
                    configureGatling()
                    auxiliaryInformation().hasGatling = hasGatling
                }
                if (hasJmh && jmhConfiguration.enabled == null) {
                    applyJmhPlugin()
                    configureJmh()
                    auxiliaryInformation().hasJmh = hasJmh
                }
                if (hasGroovy && groovyConfiguration.enabled == null) {
                    if (spockFrameworkConfiguration.enabled != true) {
                        applyGroovyPlugin()
                    }
                    addGroovyDependency()
                    auxiliaryInformation().hasGroovy = hasGroovy
                }
                if (hasGroovyTests && groovyConfiguration.enabled == null) {
                    if (spockFrameworkConfiguration.enabled != true) {
                        applyGroovyPlugin()
                        addGroovyTestsDependency()
                    }
                    auxiliaryInformation().hasGroovyTests = hasGroovyTests
                }
                if (hasScala && scalaConfiguration.enabled == null) {
                    applyScalaPlugin()
                    addScalaDependency()
                    auxiliaryInformation().hasScala = hasScala
                }
                if (hasScalaTests && scalaConfiguration.enabled == null) {
                    applyScalaPlugin()
                    addScalaTestsDependency()
                    auxiliaryInformation().hasScalaTests = hasScalaTests
                }
                if (hasKotlin && kotlinConfiguration.enabled == null) {
                    applyKotlinPlugin()
                    addKotlinDependency()
                    auxiliaryInformation().hasKotlin = hasKotlin
                }
                if (hasKotlinTests && kotlinConfiguration.enabled == null) {
                    applyKotlinPlugin()
                    addKotlinTestsDependency()
                    auxiliaryInformation().hasKotlinTests = hasKotlinTests
                }
                if (hasWeb && webConfiguration.enabled == null) {
                    configureWeb()
                    auxiliaryInformation().hasWeb = true
                }
            }
            calculateVersion()
            addModules()
            substituteDependencies()
            configureJava()
            if (ideaConfiguration.enabled == true) {
                configureIdea()
            }
            logResultingConfiguration()
            runAfterConfiguringAction()
            attention("End of ART project configuring")
        }
    }
}