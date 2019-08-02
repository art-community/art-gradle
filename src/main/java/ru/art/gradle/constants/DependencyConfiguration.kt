package ru.art.gradle.constants

enum class DependencyConfiguration(val configuration: String) {
    DEFAULT("default"),
    ANNOTATION_PROCESSOR("annotationProcessor"),
    COMPILE_CLASSPATH("compileClasspath"),
    COMPILE_ONLY("compileOnly"),
    RUNTIME_CLASSPATH("runtimeClasspath"),
    COMPILE("compile"),
    IMPLEMENTATION("implementation"),
    TEST_IMPLEMENTATION("testImplementation"),
    EMBEDDED("embedded"),
    PROVIDED("provided"),
    TEST_COMPILE_CLASSPATH("testCompileClasspath"),
    TEST_RUNTIME_CLASSPATH("testRuntimeClasspath"),
    CLASSPATH("classpath"),
    GATLING("gatling"),
    API("api")
}
