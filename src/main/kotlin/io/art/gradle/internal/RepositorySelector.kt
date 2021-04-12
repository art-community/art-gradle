package io.art.gradle.internal

import org.eclipse.jgit.api.Git
import org.gradle.api.Project

val Project.artifactsRepositoryUrl
    get() = when (Git.open(projectDir).repository.branch) {
        MAIN_BRANCH -> STABLE_MAVEN_REPOSITORY
        else -> UNSTABLE_MAVEN_REPOSITORY
    }
