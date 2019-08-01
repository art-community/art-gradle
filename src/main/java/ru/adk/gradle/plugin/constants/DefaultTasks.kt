package ru.adk.gradle.plugin.constants

object DefaultTasks {
    const val COMPILE_JAVA = "compileJava"
    const val COMPILE_TEST_JAVA = "compileTestJava"
    const val JAR = "jar"
    const val BUILD = "build"
    const val CLEAN = "clean"
    const val TEST = "test"
    const val CHECK = "check"
    const val CHECKSTYLE_MAIN = "checkstyleMain"
    const val CHECKSTYLE_TEST = "checkstyleTest"
    const val UPLOAD_ARCHIVES = "uploadArchives"
    const val UPLOAD_REPORTS = "uploadReports"
    const val JMH_COMPILE_GENERATED_CLASSES = "jmhCompileGeneratedClasses"
    const val GATLING_RUN = "gatlingRun"
}