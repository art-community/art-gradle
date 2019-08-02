package ru.art.gradle.constants

import org.gradle.api.*
import ru.art.gradle.dependency.*
import ru.art.gradle.context.Context.projectConfiguration
import ru.art.gradle.dependency.*

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

