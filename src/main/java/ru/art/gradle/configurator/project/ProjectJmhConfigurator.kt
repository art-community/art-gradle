package ru.art.gradle.configurator.project

import me.champeau.gradle.*
import org.gradle.api.*
import org.gradle.api.file.DuplicatesStrategy.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.JmhResultFormat.*
import ru.art.gradle.logging.*
import ru.art.gradle.logging.LogMessageColor.*
import ru.art.gradle.provider.*


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