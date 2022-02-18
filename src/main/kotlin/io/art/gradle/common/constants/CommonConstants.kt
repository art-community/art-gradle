package io.art.gradle.common.constants

const val EMPTY_STRING = ""
const val SPACE = " "
const val SHELL_AND = "&&"
const val NEW_LINE = "\n"
const val DOT = "."
const val DOLLAR = "$"
const val SEMICOLON = ";"
const val COLON = ":"
const val MAIN_VERSION = "main"
const val SLASH = "/"
const val STAR = "*"

const val ART = "art"
const val MAVEN_REPOSITORY = "https://nexus.art-platform.io/repository/art-maven/"

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
const val MAIN = "main"
const val TEST_EXECUTION = "testExecution"
const val TEST_EXECUTABLE = "test-executable"

const val OS_ARCH_PROPERTY = "os.arch"
const val CONFIGURATION = "configuration"
const val DOT_EXE = ".exe"
const val DOT_JAR = ".jar"
const val DOT_LOCK = ".lock"
const val DOT_STOP = ".stop"
const val POWERSHELL = "powershell"
const val BIN = "bin"

const val TAR = "tar"
const val TAR_EXTRACT_ZIP_OPTIONS = "xzf"
const val TAR_DIRECTORY_OPTION = "-C"

const val MODULE_YML = "module.yml"

val LOG_TEMPLATE = { context: String, line: String -> "($context): $line" }

const val KOTLIN_JVM_PLUGIN_ID = "org.jetbrains.kotlin.jvm"
const val KOTLIN_COMPILE_TASK_CLASS = "org.jetbrains.kotlin.gradle.tasks.KotlinCompile"

const val LOCAL_PROPERTIES_FILE = "local.properties"

const val GRAAL_DEPENDENCY_GROUP = "org.graalvm.nativeimage"
const val GRAAL_DEPENDENCY_ARTIFACT = "svm"

const val LOMBOK_DEPENDENCY_GROUP = "org.projectlombok"
const val LOMBOK_DEPENDENCY_ARTIFACT = "lombok"
