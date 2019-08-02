import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.*

val compileKotlin: KotlinCompile by tasks
val compileTestKotlin: KotlinCompile by tasks
val jar: Jar by tasks
val embedded by configurations.creating
configurations.compileClasspath.get().extendsFrom(embedded)


tasks.withType<Wrapper> {
    gradleVersion = "5.5.1"
}

plugins {
    checkstyle
    `java-gradle-plugin`
    java
    `java-library`
    kotlin("jvm") version "1.3.31"
    maven
}

group = "ru.art"
version = "1.0"

repositories {
    mavenCentral()
    jcenter()
    gradlePluginPortal()
    maven {
        url = uri("https://repo.gradle.org/gradle/libs-releases-local/")
    }
}

dependencies {
    annotationProcessor("org.projectlombok", "lombok", "1.18.+")
    compileClasspath("org.projectlombok", "lombok", "1.18.+")
    testImplementation("org.projectlombok", "lombok", "1.18.+")

    implementation("org.gradle", "gradle-kotlin-dsl", "1.1.3").exclude("org.jetbrains.kotlinx", "kotlinx-metadata-jvm")
    implementation("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx", "kotlinx-metadata-jvm", "0.0.+")

    embedded("org.jfrog.artifactory.client", "artifactory-java-client-services", "2.7.0")
            .exclude("ch.qos.logback", "logback-classic")
            .exclude("org.codehaus.groovy", "groovy")
    embedded("org.eclipse.jgit", "org.eclipse.jgit", "5.3.1.201904271842-r")
    embedded("gradle.plugin.com.github.lkishalmi.gatling", "gradle-gatling-plugin", "3.0.+")
    embedded("me.champeau.gradle", "jmh-gradle-plugin", "0.4.+")
    embedded("org.jetbrains.kotlin", "kotlin-gradle-plugin", "1.3.31")
    embedded("com.google.protobuf", "protobuf-gradle-plugin", "0.8+")
    embedded("net.sf.proguard", "proguard-gradle", "6.1.+")
}

compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
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


gradlePlugin {
    plugins {
        create("artProjectPlugin") {
            id = "artProject"
            implementationClass = "ru.art.gradle.plugin.ProjectPlugin"
        }
        create("artSettingsPlugin") {
            id = "artSettings"
            implementationClass = "ru.art.gradle.plugin.SettingsPlugin"
        }
    }
}