package ru.adk.gradle.plugin.configurator.project

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension
import ru.adk.gradle.plugin.ProjectPlugin
import ru.adk.gradle.plugin.constants.CHECK_STYLE_CONFIGURATION
import ru.adk.gradle.plugin.constants.CUSTOM_STYLESHEET
import ru.adk.gradle.plugin.constants.MAIN_SOURCE_SET
import ru.adk.gradle.plugin.constants.TEST_SOURCE_SET
import ru.adk.gradle.plugin.context.Context.projectConfiguration
import ru.adk.gradle.plugin.provider.buildTask
import ru.adk.gradle.plugin.provider.checkstyleMainTask
import ru.adk.gradle.plugin.provider.checkstyleTestTask
import ru.adk.gradle.plugin.provider.testTask

fun Project.addCheckstyle() {
    extensions.configure(CheckstyleExtension::class.java) { extension ->
        with(extension) {
            toolVersion = projectConfiguration().externalDependencyVersionsConfiguration.checkstyleVersion
            config = resources.text.fromString(ProjectPlugin::class.java.classLoader.getResourceAsStream(CHECK_STYLE_CONFIGURATION)?.reader()?.readText()
                    ?: return@with)
            isIgnoreFailures = projectConfiguration().checkstyleConfiguration.ignoreFailures
        }
    }

    with(convention.getPlugin(JavaPluginConvention::class.java)) {
        checkstyleMainTask().source = sourceSets.getByName(MAIN_SOURCE_SET).allSource
        checkstyleTestTask().source = sourceSets.getByName(TEST_SOURCE_SET).allSource
    }

    buildTask().dependsOn(checkstyleMainTask())
    testTask().dependsOn(checkstyleTestTask())

    tasks.withType(Checkstyle::class.java).configureEach { checkstyle ->
        checkstyle.reports { reports ->
            reports.xml.isEnabled = false
            reports.html.isEnabled = true
            reports.html.stylesheet = resources.text.fromString(ProjectPlugin::class.java.classLoader.getResourceAsStream(CUSTOM_STYLESHEET)?.reader()?.readText()
                    ?: return@reports)
        }
    }
}