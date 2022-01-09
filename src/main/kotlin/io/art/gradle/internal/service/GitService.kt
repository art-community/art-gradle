package io.art.gradle.internal.service

import io.art.gradle.common.constants.MAIN_VERSION
import io.art.gradle.common.constants.STABLE_MAVEN_REPOSITORY
import io.art.gradle.internal.constants.UNSTABLE_MAVEN_REPOSITORY
import org.gradle.api.Project

val Project.dependencyRepositoryUrl get() = STABLE_MAVEN_REPOSITORY
val Project.publishingRepositoryUrl
    get() = when (rootProject.version) {
        MAIN_VERSION -> STABLE_MAVEN_REPOSITORY
        else -> UNSTABLE_MAVEN_REPOSITORY
    }
