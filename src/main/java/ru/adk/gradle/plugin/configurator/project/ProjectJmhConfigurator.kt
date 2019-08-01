package ru.adk.gradle.plugin.configurator.project

import me.champeau.gradle.JMHPluginExtension
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy.WARN
import ru.adk.gradle.plugin.constants.ADDITIONAL_LOGGING_MESSAGE_INDENT
import ru.adk.gradle.plugin.constants.JmhResultFormat.JSON
import ru.adk.gradle.plugin.logging.LogMessageColor.PURPLE_BOLD
import ru.adk.gradle.plugin.logging.message
import ru.adk.gradle.plugin.logging.success
import ru.adk.gradle.plugin.provider.buildTask
import ru.adk.gradle.plugin.provider.jmhCompileGeneratedClassesTask


fun Project.configureJmh() {
    extensions.configure(JMHPluginExtension::class.java) { jmh ->
        with(jmh) {
            duplicateClassesStrategy = WARN
            resultFormat = JSON.name
        }
    }
    jmhCompileGeneratedClassesTask().dependsOn(buildTask())

    success("Configuring JMH:\n" + message("""
        duplicateClassesStrategy = WARN
        resultFormat = JSON.name
        (!) jmhCompileGeneratedClassesTask depends on build
        """.replaceIndent(ADDITIONAL_LOGGING_MESSAGE_INDENT), PURPLE_BOLD))

}