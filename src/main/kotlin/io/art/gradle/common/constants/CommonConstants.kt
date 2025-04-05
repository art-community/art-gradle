package io.art.gradle.common.constants

import org.gradle.internal.os.OperatingSystem

const val EMPTY_STRING = ""
const val SPACE = " "
const val NEW_LINE = "\n"
const val DOT = "."
const val DOLLAR = "$"
const val SEMICOLON = ";"
const val COLON = ":"
const val MAIN_VERSION = "main"
const val SLASH = "/"
const val STAR = "*"

const val ART = "art"
const val JAR = "jar"
const val ZIP = "zip"
const val PACKAGE = "package"
const val MAVEN_REPOSITORY = "https://repo.repsy.io/mvn/antonsh/art-packages/"

const val ANNOTATION_PROCESSOR_CONFIGURATION_NAME = "annotationProcessor"
const val COMPILE_CLASS_PATH_CONFIGURATION_NAME = "compileClasspath"
const val COMPILE_ONLY_CONFIGURATION_NAME = "compileOnly"
const val EMBEDDED_CONFIGURATION_NAME = "embedded"
const val TEST_EMBEDDED_CONFIGURATION_NAME = "testEmbedded"
const val IMPLEMENTATION_CONFIGURATION_NAME = "implementation"
const val TEST_IMPLEMENTATION_CONFIGURATION_NAME = "testImplementation"
const val API_CONFIGURATION_NAME = "api"

const val BUILD = "build"
const val CLEAN = "clean"

const val EXECUTABLE = "executable"
const val DEPENDENCIES = "dependencies"
const val TEST = "test"
const val TEST_EXECUTION = "testExecution"
const val TEST_EXECUTABLE = "test-executable"

const val DOT_JAR = ".jar"
const val DOT_LOCK = ".lock"
const val MODULE_YML = "module.yml"

val LOG_TEMPLATE = { context: String, line: String -> "($context): $line" }

const val KOTLIN_JVM_PLUGIN_ID = "org.jetbrains.kotlin.jvm"
const val KOTLIN_COMPILE_TASK_CLASS = "org.jetbrains.kotlin.gradle.tasks.KotlinCompile"

const val LOCAL_PROPERTIES_FILE = "local.properties"


const val LOMBOK_DEPENDENCY_GROUP = "org.projectlombok"
const val LOMBOK_DEPENDENCY_ARTIFACT = "lombok"

const val BACKWARD_SLASH = "\\"
const val BACKWARD_SLASH_REGEX = "\\\\"
const val WINDOWS_DISK_PATH_SLASH = ":/"
const val WINDOWS_DISK_PATH_BACKWARD_SLASH = ":\\"
const val WINDOWS_DISK_PATH_BACKWARD_SLASH_REGEX = ":\\\\"
const val WSL_DISK_PREFIX = "/mnt/"

fun bashCommand(vararg arguments: String) = arrayOf("bash", "-c", arguments.joinToString(SPACE))

fun String.wsl(): String {
    var converted = this
    if (!OperatingSystem.current().isWindows) return converted
    if (converted.isEmpty()) return converted
    if (SLASH == EMPTY_STRING + converted[0] || BACKWARD_SLASH == EMPTY_STRING + converted[0]) {
        converted = converted.substring(1)
    }
    if (converted.contains(WINDOWS_DISK_PATH_SLASH) || converted.contains(WINDOWS_DISK_PATH_BACKWARD_SLASH)) {
        converted = converted
                .replace(WINDOWS_DISK_PATH_SLASH.toRegex(), SLASH)
                .replace(WINDOWS_DISK_PATH_BACKWARD_SLASH_REGEX.toRegex(), SLASH)
                .replace(BACKWARD_SLASH_REGEX.toRegex(), SLASH)
        val firstLetter: String = EMPTY_STRING + converted[0]
        return WSL_DISK_PREFIX + firstLetter.lowercase() + converted.substring(1)
    }
    return converted
}
