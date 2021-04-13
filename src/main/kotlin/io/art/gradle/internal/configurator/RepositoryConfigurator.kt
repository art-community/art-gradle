package io.art.gradle.internal.configurator

import io.art.gradle.internal.service.dependencyRepositoryUrl
import org.gradle.api.Project
import org.gradle.kotlin.dsl.repositories

fun Project.configureRepositories() = allprojects {
    repositories {
        maven {
            url = uri(dependencyRepositoryUrl)
        }
    }
}
