package io.art.gradle.internal.service

import io.art.gradle.common.constants.MAVEN_REPOSITORY
import org.gradle.api.Project

val Project.dependencyRepositoryUrl get() = MAVEN_REPOSITORY
val Project.publishingRepositoryUrl get() = MAVEN_REPOSITORY
