/*
 * Copyright 2019 ART
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.art.gradle.configurator.project

import org.gradle.api.*
import org.gradle.api.tasks.*
import ru.art.gradle.constants.*
import ru.art.gradle.context.Context.projectConfiguration
import ru.art.gradle.exception.*
import ru.art.gradle.logging.*
import ru.art.gradle.logging.LogMessageColor.*
import ru.art.gradle.provider.*
import java.io.*
import java.io.File.*
import java.lang.Runtime.*

fun Project.configureWeb() = ignoreException {
    getRuntime().exec(NPM)
    val prepareWeb = tasks.create(PREPARE_WEB, Exec::class.java) { task ->
        task.group = WEB_TASK_GROUP

        with(task) {
            workingDir = file("$projectDir$separator$WEB_SOURCE_SET")
            commandLine = PREPARE_WEB_COMMAND
            standardOutput = ByteArrayOutputStream()
        }
    }

    val buildWeb = tasks.create(BUILD_WEB, Exec::class.java) { task ->
        task.group = WEB_TASK_GROUP

        with(task) {
            workingDir = file("$projectDir$separator$WEB_SOURCE_SET")
            commandLine = BUILD_WEB_COMMAND
            standardOutput = ByteArrayOutputStream()
        }
    }

    val cleanWeb = tasks.create(CLEAN_WEB) { task ->
        task.group = WEB_TASK_GROUP

        task.doLast {
            delete(WEB_SOURCE_SET_DIST)
        }
    }

    buildWeb.dependsOn(prepareWeb)
    jarTask().dependsOn(buildWeb)
    cleanTask().dependsOn(cleanWeb)
    buildTask().dependsOn(buildWeb)

    projectConfiguration().resourcesConfiguration.resourceDirs.add(WEB_SOURCE_SET_DIST)

    success("Configuring Web:\n" + message("""
        (!) prepareWeb task runs 'npm install' command
        (!) buildWeb task runs 'npm run production' command
        (!) cleanWeb will delete '$WEB_SOURCE_SET_DIST' 
        (!) directory '$WEB_SOURCE_SET_DIST' applied as resourcesDir 
        (!) buildWeb depends on prepareWeb
        (!) jar depends on buildWeb
        (!) clean depends on cleanWeb
        (!) build depends on buildWeb
    """.replaceIndent(ADDITIONAL_LOGGING_MESSAGE_INDENT), PURPLE_BOLD))
}