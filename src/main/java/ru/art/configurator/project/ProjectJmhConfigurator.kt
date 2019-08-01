package ru.art.configurator.project

import me.champeau.gradle.JMHPluginExtension
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy.WARN
import ru.art.constants.ADDITIONAL_LOGGING_MESSAGE_INDENT
import ru.art.constants.JmhResultFormat.JSON
import ru.art.logging.LogMessageColor.PURPLE_BOLD
import ru.art.logging.message
import ru.art.logging.success
import ru.art.provider.buildTask
import ru.art.provider.jmhCompileGeneratedClassesTask


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