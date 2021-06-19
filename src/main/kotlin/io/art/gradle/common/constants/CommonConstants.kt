package io.art.gradle.common.constants

const val EMPTY_STRING = ""
const val SPACE = " "
const val NEW_LINE = "\n"
const val DOT = "."
const val DOLLAR = "$"
const val SEMICOLON = ";"
const val COLON = ":"
const val MAIN_BRANCH = "main"

const val ART = "art"
const val STABLE_MAVEN_REPOSITORY = "https://nexus.art-platform.io/repository/art-maven-stable/"

const val COMPILE_CLASS_PATH_CONFIGURATION_NAME = "compileClasspath"
const val EMBEDDED_CONFIGURATION_NAME = "embedded"
const val IMPLEMENTATION_CONFIGURATION_NAME = "implementation"

const val BUILD = "build"
const val CLEAN = "clean"

const val EXECUTABLE = "executable"

const val OS_ARCH_PROPERTY = "os.arch"
const val CONFIGURATION = "configuration"
const val DOT_EXE = ".exe"
const val DOT_JAR = ".jar"
const val DOT_LOCK = ".lock"
const val POWERSHELL = "powershell"

const val TAR = "tar"
const val TAR_EXTRACT_ZIP_OPTIONS = "xzf"
const val TAR_DIRECTORY_OPTION = "-C"

const val MODULE_YML = "module.yml"

val LOG_TEMPLATE = { context: String, line: String -> "($context): $line" }
