package io.art.gradle.common.constants

const val JAVA = "java"
const val EMPTY_STRING = ""
const val SPACE = " "
const val NEW_LINE = "\n"
const val ART = "art"
const val STABLE_MAVEN_REPOSITORY = "https://nexus.art-platform.io/repository/art-maven-stable/"
const val LUA = "lua"
const val AMALG_LUA = "amalg.lua"
const val LUA_SOURCE_SET = "src/main/lua"
const val AMALG_RELATIVE_PATH = "$LUA/$AMALG_LUA"
const val DOT = "."
const val DOT_LUA = ".lua"
const val DESTINATION = "destination"
const val BUILD = "build"
const val CLEAN = "clean"
const val DOT_INIT = ".init"
const val LUA_OUTPUT_FLAG = "-o"

const val BUILD_EXECUTABLE_JAR_TASK = "build-executable-jar"
const val BUILD_EXECUTABLE_NATIVE_TASK = "build-executable-native"

const val RUN_EXECUTABLE_JAR_TASK = "run-executable-jar"
const val RUN_EXECUTABLE_NATIVE_TASK = "run-executable-native"
const val RUN_WITH_NATIVE_IMAGE_AGENT = "run-executable-with-native-agent"

const val EMBEDDED_CONFIGURATION_NAME = "embedded"
const val IMPLEMENTATION_CONFIGURATION_NAME = "implementation"

const val MAIN_CLASS_MANIFEST_ATTRIBUTE = "Main-Class"
const val MULTI_RELEASE_MANIFEST_ATTRIBUTE = "Multi-Release"

const val JAR = "jar"
const val EXECUTABLE = "executable"


const val DOLLAR = "$"
const val OS_ARCH_PROPERTY = "os.arch"
const val CONFIGURATION = "configuration"
const val DOT_EXE = ".exe"
const val DOT_JAR = ".jar"
const val DOT_LOCK = ".lock"
const val POWERSHELL = "powershell"
const val JAR_OPTION = "-jar"

const val TAR = "tar"
const val TAR_EXTRACT_ZIP_OPTIONS = "xzf"
const val TAR_DIRECTORY_OPTION = "-C"


val DEFAULT_JAR_EXCLUSIONS = setOf("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/**.RSA", "META-INF/MANIFEST.MF")

val LOG_TEMPLATE = { context: String, line: String -> "($context): $line" }
