package io.art.gradle.common.constants

const val EMPTY_STRING = ""
const val NEW_LINE = "\n"
const val ART = "art"
const val STABLE_MAVEN_REPOSITORY = "https://nexus.art-platform.io/repository/art-maven-stable/"
val LOG_TEMPLATE = { context: String, line: String -> "($context): $line" }
