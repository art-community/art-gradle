package ru.art.gradle.configurator.project

import org.gradle.api.*
import org.gradle.api.plugins.*
import org.gradle.api.plugins.quality.*
import ru.art.gradle.*
import ru.art.gradle.constants.*
import ru.art.gradle.context.Context.projectConfiguration
import ru.art.gradle.provider.*

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