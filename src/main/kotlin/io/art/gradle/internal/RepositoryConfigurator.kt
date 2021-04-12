package io.art.gradle.internal

import org.gradle.api.Project
import org.gradle.kotlin.dsl.repositories

fun Project.configureRepositories() = repositories {
    maven {
        url = uri(artifactsRepositoryUrl)
    }
}
