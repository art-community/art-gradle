package ru.adk.gradle.plugin.constants

import ru.adk.gradle.plugin.constants.AdkMajorVersion.*
import ru.adk.gradle.plugin.constants.DependencyVersionSelectionMode.*
import ru.adk.gradle.plugin.model.*

const val EMPTY_STRING = ""
const val STAR = '*'
const val COMMA = ","
const val DOT = "."
const val SLASH = "/"
const val NEW_LINE = "\n"
const val PREVIOUS_DIRECTORY = "../"
const val SEMICOLON = ":"
const val GRADLE_VERSION_5 = "5"
const val ADK_EXTENSION = "adk"
const val MAIN_SOURCE_SET = "main"
const val TEST_SOURCE_SET = "test"
const val SRC = "src"
const val JAR_EXTENSION = ".jar"
const val ADK_MODULE_GROUP = "ru.adk"
val ADK_MODULE_GROUP_SELECTOR = { group: String, mode: DependencyVersionSelectionMode, version: Any? ->
    when (mode) {
        BRANCH -> {
            when (version as String?) {
                "release-candidate" -> "ru.rti.development"
                "release" -> "ru.rti.development"
                else -> "ru.adk"
            }
        }
        TAG -> {
            val tagVersion = version as DependencyTagVersion
            when {
                tagVersion.tag.matches(Regex("1.\\d")) -> "ru.rti.development"
                tagVersion.tag.matches(Regex("2.\\d")) -> "ru.rti.development"
                tagVersion.tag.matches(Regex("3.\\d")) -> "ru.rti.development"
                else -> "ru.adk"
            }
        }
        MANUAL -> group
        LATEST -> "ru.adk"
        MAJOR -> {
            when (version as AdkMajorVersion?) {
                RELEASE_1 -> "ru.rti.development"
                RELEASE_2 -> "ru.rti.development"
                RELEASE_3 -> "ru.rti.development"
                else -> group
            }
        }
    }
}
const val CLASS_FILE_EXTENSION = ".class"
const val JAVA_FILE_EXTENSION = ".java"
const val ADDITIONAL_LOGGING_MESSAGE_INDENT = "  "
const val ADK_PROJECT = "adkProject"
const val ADK_SETTINGS = "adkSettings"
const val ADK_PLUGIN_DEPENDENCY = "ru.adk:application-gradle-plugin"