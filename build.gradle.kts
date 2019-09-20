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

import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.concurrent.TimeUnit.SECONDS

val compileKotlin: KotlinCompile by tasks
val compileTestKotlin: KotlinCompile by tasks
val jar: Jar by tasks
val embedded: Configuration by configurations.creating
configurations.compileClasspath.get().extendsFrom(embedded)

plugins {
    kotlin("jvm") version "1.3.31"
    checkstyle
    `java-gradle-plugin`
    java
    `java-library`
    `maven-publish`
    signing
    id("com.gradle.plugin-publish") version "0.10.0"
}

group = "io.github.art"

tasks.withType(Wrapper::class.java) {
    gradleVersion = "5.6"
}

compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

repositories {
    mavenCentral()
    jcenter()
    gradlePluginPortal()
    maven {
        url = uri("https://repo.gradle.org/gradle/libs-releases-local/")
    }
}

pluginBundle {
    website = "https://github.com/art-community/application-gradle-plugin"
    vcsUrl = "https://github.com/art-community/application-gradle-plugin.git"
    tags = listOf("art",
            "ART",
            "configuring",
            "application",
            "java",
            "kotlin",
            "groovy",
            "spock",
            "spock framework",
            "jmh",
            "gatling")
}
gradlePlugin {
    plugins {
        create("io.github.art.project") {
            id = "io.github.art.project"
            displayName = "Plugin for simplify project configuration"
            description = "A plugin that helps you to configure JVM projects"
            implementationClass = "ru.art.gradle.ProjectPlugin"
        }
        create("io.github.art.settings") {
            id = "io.github.art.settings"
            displayName = "Plugin for simplify settings configuration"
            description = "A plugin that helps you to configure JVM projects"
            implementationClass = "ru.art.gradle.SettingsPlugin"
        }
    }
}

dependencies {
    annotationProcessor("org.projectlombok", "lombok", "1.18.+")
    compileClasspath("org.projectlombok", "lombok", "1.18.+")
    testImplementation("org.projectlombok", "lombok", "1.18.+")

    compileOnly("org.gradle", "gradle-kotlin-dsl", "1.1.3").exclude("org.jetbrains.kotlinx", "kotlinx-metadata-jvm")
    compileOnly("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    compileOnly("org.jetbrains.kotlinx", "kotlinx-metadata-jvm", "0.1.0")
    compileOnly("io.github.art", "application-generator", "1+")

    embedded("org.eclipse.jgit", "org.eclipse.jgit", "5.3.1.201904271842-r")
    embedded("me.champeau.gradle", "jmh-gradle-plugin", "0.4.+")
    embedded("org.jetbrains.kotlin", "kotlin-gradle-plugin", "1.3.31")
    embedded("com.google.protobuf", "protobuf-gradle-plugin", "0.8+")
}

configurations.all {
    resolutionStrategy(closureOf<ResolutionStrategy> {
        cacheChangingModulesFor(1, SECONDS)
    })
}

dependencies.components.all {
    isChanging = true
}


with(jar) {
    isZip64 = true
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    embedded
            .files
            .filter { !it.name.contains("kotlin-build-common") }
            .map { if (it.isDirectory) fileTree(it) else zipTree(it) }
            .forEach { from(it) }

    zipTree(configurations.getByName("embedded")
            .files
            .first { it.name.contains("kotlin-build-common") })
            .filter { !it.name.contains("OutputItemsCollectorImpl") }
            .forEach { from(it) }

    exclude(listOf("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA"))
}