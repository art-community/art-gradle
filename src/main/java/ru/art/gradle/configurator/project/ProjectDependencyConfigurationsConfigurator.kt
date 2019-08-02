package ru.art.gradle.configurator.project

import org.gradle.api.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.DependencyConfiguration.*
import ru.art.gradle.logging.*
import ru.art.gradle.logging.LogMessageColor.*

fun Project.addDependencyConfigurations(): Unit = configurations.run {
    create(EMBEDDED.configuration)
    create(PROVIDED.configuration)

    getByName(COMPILE_CLASSPATH.configuration).extendsFrom(getByName(EMBEDDED.configuration), getByName(PROVIDED.configuration))
    getByName(API.configuration).extendsFrom(getByName(EMBEDDED.configuration))
    getByName(TEST_COMPILE_CLASSPATH.configuration).extendsFrom(getByName(EMBEDDED.configuration), getByName(PROVIDED.configuration))

    success("Created two ART gradle configurations ${message("(embedded and provided)", CYAN_BOLD)}:$NEW_LINE" + message("""
            (*) compileClasspath extends from embedded
            (*) api extends from embedded
            (*) testCompileClasspath extends from embedded
            (*) compileClasspath extends from provided
            (*) api extends from provided
            (*) testCompileClasspath extends from provided
            """.replaceIndent(ADDITIONAL_LOGGING_MESSAGE_INDENT), PURPLE_BOLD) + NEW_LINE +
            message("""(!) embedded dependency artifacts will be included inside .jar""".replaceIndent(ADDITIONAL_LOGGING_MESSAGE_INDENT), GREEN_BOLD))
}