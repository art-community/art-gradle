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

package ru.art.gradle.constants

import org.gradle.api.*
import ru.art.gradle.dependency.*
import ru.art.gradle.context.Context.projectConfiguration

fun Project.lombok() = Dependency("org.projectlombok", "lombok", projectConfiguration().externalDependencyVersionsConfiguration.lombokVersion)
fun Project.groovy() = Dependency("org.codehaus.groovy", "groovy-all", projectConfiguration().externalDependencyVersionsConfiguration.groovyVersion)
fun Project.spock() = Dependency("org.spockframework", "spock-core", projectConfiguration().externalDependencyVersionsConfiguration.spockVersion)
fun Project.cglib() = Dependency("cglib", "cglib-nodep", projectConfiguration().externalDependencyVersionsConfiguration.cglibVersion)
fun Project.kotlin() = Dependency("org.jetbrains.kotlin", "kotlin-stdlib-jdk8", projectConfiguration().externalDependencyVersionsConfiguration.kotlinVersion)
fun Project.scala() = Dependency("org.scala-lang", "scala-library", projectConfiguration().externalDependencyVersionsConfiguration.scalaVersion)
fun Project.logbackClassic() = Dependency("ch.qos.logback", "logback-classic", projectConfiguration().externalDependencyVersionsConfiguration.logbackVersion)
fun Project.junit() = Dependency("junit", "junit", projectConfiguration().externalDependencyVersionsConfiguration.junitVersion)
fun Project.generator() = Dependency(projectConfiguration().generatorConfiguration.group, "application-generator", projectConfiguration().generatorConfiguration.version.ifEmpty { project.version } as String)
fun Project.gatlingHttp() = Dependency(
        group = "io.gatling",
        artifact = "gatling-http",
        version = projectConfiguration().externalDependencyVersionsConfiguration.gatlingVersion,
        exclusions = setOf(logbackClassic()))

fun Project.gatlingCore() = Dependency("io.gatling",
        "gatling-core",
        projectConfiguration().externalDependencyVersionsConfiguration.gatlingVersion,
        exclusions = setOf(logbackClassic()))

