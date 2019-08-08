import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.*

val compileKotlin: KotlinCompile by tasks
val compileTestKotlin: KotlinCompile by tasks
val jar: Jar by tasks
val embedded: Configuration by configurations.creating
configurations.compileClasspath.get().extendsFrom(embedded)

tasks.withType<Wrapper> {
    gradleVersion = "5.5.1"
}

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
version = "1.0.20"

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

    embedded("org.eclipse.jgit", "org.eclipse.jgit", "5.3.1.201904271842-r")
    embedded("gradle.plugin.com.github.lkishalmi.gatling", "gradle-gatling-plugin", "3.0.+")
    embedded("me.champeau.gradle", "jmh-gradle-plugin", "0.4.+")
    embedded("org.jetbrains.kotlin", "kotlin-gradle-plugin", "1.3.31")
    embedded("com.google.protobuf", "protobuf-gradle-plugin", "0.8+")
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