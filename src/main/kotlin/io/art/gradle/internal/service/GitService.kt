package io.art.gradle.internal.service

import io.art.gradle.internal.constants.MAIN_BRANCH
import io.art.gradle.internal.constants.STABLE_MAVEN_REPOSITORY
import io.art.gradle.internal.constants.UNSTABLE_MAVEN_REPOSITORY
import org.eclipse.jgit.api.Git
import org.gradle.api.Project

val Project.git: Git get() = Git.open(rootDir)
val Project.branch: String get() = git.repository.branch
val Project.dependencyRepositoryUrl get() = STABLE_MAVEN_REPOSITORY
val Project.publishingRepositoryUrl
    get() = when (git.repository.branch) {
        MAIN_BRANCH -> STABLE_MAVEN_REPOSITORY
        else -> UNSTABLE_MAVEN_REPOSITORY
    }
