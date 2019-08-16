/*
 * ART Java
 *
 * Copyright 2019 ART
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.art.gradle.configurator.project

import org.gradle.api.*
import org.gradle.api.plugins.*
import org.gradle.api.plugins.quality.*
import ru.art.gradle.*
import ru.art.gradle.constants.*
import ru.art.gradle.context.Context.projectExtension
import ru.art.gradle.provider.*

fun Project.addCheckstyle() {
    extensions.configure(CheckstyleExtension::class.java) { extension ->
        with(extension) {
            toolVersion = projectExtension().externalDependencyVersionsConfiguration.checkstyleVersion
            config = resources.text.fromString(ProjectPlugin::class.java.classLoader.getResourceAsStream(CHECK_STYLE_CONFIGURATION)?.reader()?.readText()
                    ?: return@with)
            isIgnoreFailures = projectExtension().checkstyleConfiguration.ignoreFailures
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