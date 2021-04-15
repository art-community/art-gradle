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
const val ART_EXAMPLE = "art-example"
const val JAVA = "java"
const val ART_COMMUNITY_REPOSITORY = "github.com/art-community"
const val ART_COMMUNITY_URL = "https://$ART_COMMUNITY_REPOSITORY"
const val ART = "art"
const val LUA = "lua"
const val LUA_SOURCE_SET = "src/main/lua"
const val BUNDLER_NAME = "amalg.lua"
const val BUNDLER_RELATIVE_PATH = "$LUA/$BUNDLER_NAME"
const val DOT = "."
const val DOT_LUA = ".lua"
const val DESTINATION = "destination"
const val BUILD = "build"
const val CLEAN = "clean"
const val DOT_INIT = ".init"
val Project.gitUrl get() = "$ART_COMMUNITY_URL/${rootProject.name}"
