package io.art.gradle.internal.constants

import org.gradle.api.Project

const val UNSTABLE_MAVEN_REPOSITORY = "https://nexus.art-platform.io/repository/art-maven-unstable"
const val ART_JAVA = "art-java"
const val ART_GENERATOR = "art-generator"
const val ART_KOTLIN = "art-kotlin"
const val ART_EXAMPLE = "art-example"
const val ART_COMMUNITY_REPOSITORY = "github.com/art-community"
const val ART_COMMUNITY_URL = "https://$ART_COMMUNITY_REPOSITORY"

val Project.gitUrl get() = "$ART_COMMUNITY_URL/${rootProject.name}"
