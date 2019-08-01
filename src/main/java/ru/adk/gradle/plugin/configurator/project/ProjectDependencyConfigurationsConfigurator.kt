package ru.adk.gradle.plugin.configurator.project

import org.gradle.api.Project
import ru.adk.gradle.plugin.constants.ADDITIONAL_LOGGING_MESSAGE_INDENT
import ru.adk.gradle.plugin.constants.DependencyConfiguration.*
import ru.adk.gradle.plugin.constants.NEW_LINE
import ru.adk.gradle.plugin.logging.LogMessageColor.*
import ru.adk.gradle.plugin.logging.message
import ru.adk.gradle.plugin.logging.success

fun Project.addDependencyConfigurations(): Unit = configurations.run {
    create(EMBEDDED.configuration)
    create(PROVIDED.configuration)

    getByName(COMPILE_CLASSPATH.configuration).extendsFrom(getByName(EMBEDDED.configuration), getByName(PROVIDED.configuration))
    getByName(API.configuration).extendsFrom(getByName(EMBEDDED.configuration))
    getByName(TEST_COMPILE_CLASSPATH.configuration).extendsFrom(getByName(EMBEDDED.configuration), getByName(PROVIDED.configuration))

    success("Created two ADK gradle configurations ${message("(embedded and provided)", CYAN_BOLD)}:$NEW_LINE" + message("""
            (*) compileClasspath extends from embedded
            (*) api extends from embedded
            (*) testCompileClasspath extends from embedded
            (*) compileClasspath extends from provided
            (*) api extends from provided
            (*) testCompileClasspath extends from provided
            """.replaceIndent(ADDITIONAL_LOGGING_MESSAGE_INDENT), PURPLE_BOLD) + NEW_LINE +
            message("""(!) embedded dependency artifacts will be included inside .jar""".replaceIndent(ADDITIONAL_LOGGING_MESSAGE_INDENT), GREEN_BOLD))
}