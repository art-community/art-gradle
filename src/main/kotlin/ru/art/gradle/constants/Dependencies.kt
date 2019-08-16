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

package ru.art.gradle.constants

import org.gradle.api.*
import ru.art.gradle.context.Context.projectExtension
import ru.art.gradle.dependency.*

fun Project.lombok() = Dependency(
        group = "org.projectlombok",
        artifact = "lombok",
        version = projectExtension().externalDependencyVersionsConfiguration.lombokVersion)

fun Project.groovy() = Dependency(
        group = "org.codehaus.groovy",
        artifact = "groovy-all",
        version = projectExtension().externalDependencyVersionsConfiguration.groovyVersion)

fun Project.spock() = Dependency(
        group = "org.spockframework",
        artifact = "spock-core",
        version = projectExtension().externalDependencyVersionsConfiguration.spockVersion)

fun Project.cglib() = Dependency(
        group = "cglib",
        artifact = "cglib-nodep",
        version = projectExtension().externalDependencyVersionsConfiguration.cglibVersion)

fun Project.kotlin() = Dependency(
        group = "org.jetbrains.kotlin",
        artifact = "kotlin-stdlib-jdk8",
        version = projectExtension().externalDependencyVersionsConfiguration.kotlinVersion)

fun Project.scala() = Dependency(
        group = "org.scala-lang",
        artifact = "scala-library",
        version = projectExtension().externalDependencyVersionsConfiguration.scalaVersion)

fun Project.logbackClassic() = Dependency(
        group = "ch.qos.logback",
        artifact = "logback-classic",
        version = projectExtension().externalDependencyVersionsConfiguration.logbackVersion)

fun Project.junit() = Dependency(
        group = "junit",
        artifact = "junit",
        version = projectExtension().externalDependencyVersionsConfiguration.junitVersion)

fun Project.generator() = Dependency(
        group = projectExtension().generatorConfiguration.group,
        artifact = "application-generator",
        version = projectExtension().generatorConfiguration.version)

fun Project.gatlingHttp() = Dependency(
        group = "io.gatling",
        artifact = "gatling-http",
        version = projectExtension().externalDependencyVersionsConfiguration.gatlingVersion,
        exclusions = setOf(logbackClassic()))

fun Project.gatlingCore() = Dependency(
        group = "io.gatling",
        artifact = "gatling-core",
        version = projectExtension().externalDependencyVersionsConfiguration.gatlingVersion,
        exclusions = setOf(logbackClassic()))

