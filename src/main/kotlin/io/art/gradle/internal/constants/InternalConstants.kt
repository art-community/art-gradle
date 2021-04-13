package io.art.gradle.internal.constants

import org.gradle.api.Project

const val EMPTY_STRING = ""
const val NEW_LINE = "\n"
val LOG_TEMPLATE = { context: String, line: String -> "($context): $line" }
const val UNSTABLE_MAVEN_REPOSITORY = "https://nexus.art-platform.io/repository/art-maven-unstable"
const val STABLE_MAVEN_REPOSITORY = "https://nexus.art-platform.io/repository/art-maven-stable/"
const val MAIN_BRANCH = "main"
const val ART_JAVA = "art-java"
const val ART_GENERATOR = "art-generator"
const val ART_KOTLIN = "art-kotlin"
const val JAVA = "java"
const val ART_COMMUNITY_REPOSITORY = "github.com/art-community"
const val ART_COMMUNITY_URL = "https://$ART_COMMUNITY_REPOSITORY"
val Project.gitUrl get() = "$ART_COMMUNITY_URL/${rootProject.name}"
