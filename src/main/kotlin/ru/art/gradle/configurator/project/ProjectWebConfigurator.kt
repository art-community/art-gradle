/*
 *    Copyright 2019 ART 
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

import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import ru.art.gradle.constants.*
import ru.art.gradle.context.Context.projectExtension
import ru.art.gradle.exception.ignoreException
import ru.art.gradle.logging.LogMessageColor.PURPLE_BOLD
import ru.art.gradle.logging.message
import ru.art.gradle.logging.success
import ru.art.gradle.provider.buildTask
import ru.art.gradle.provider.cleanTask
import ru.art.gradle.provider.jarTask
import java.io.ByteArrayOutputStream
import java.io.File.separator
import java.lang.Runtime.getRuntime

fun Project.configureWeb() = ignoreException {
    getRuntime().exec(projectExtension().webConfiguration.buildToolCheckingCommand)
    val prepareWeb = tasks.create(PREPARE_WEB, Exec::class.java) { task ->
        task.group = WEB_TASK_GROUP

        with(task) {
            workingDir = file("$projectDir$separator${projectExtension().webConfiguration.webSourcesDir}")
            commandLine = projectExtension().webConfiguration.prepareWebCommand
            standardOutput = ByteArrayOutputStream()
        }
    }

    val buildWeb = tasks.create(BUILD_WEB, Exec::class.java) { task ->
        task.group = WEB_TASK_GROUP

        with(task) {
            workingDir = file("$projectDir$separator${projectExtension().webConfiguration.webSourcesDir}")
            commandLine = projectExtension().webConfiguration.buildWebCommand
            standardOutput = ByteArrayOutputStream()
        }
    }

    val cleanWeb = tasks.create(CLEAN_WEB) { task ->
        task.group = WEB_TASK_GROUP

        task.doLast {
            delete(projectExtension().webConfiguration.webBuildDir)
        }
    }

    buildWeb.dependsOn(prepareWeb)
    jarTask().dependsOn(buildWeb)
    cleanTask().dependsOn(cleanWeb)
    buildTask().dependsOn(buildWeb)

    projectExtension().resourcesConfiguration.resourceDirs.add(projectExtension().webConfiguration.webBuildDir)

    success("Configuring Web:\n" + message("""
        (!) prepareWeb task runs 'npm install' command
        (!) buildWeb task runs 'npm run production' command
        (!) cleanWeb will delete '${projectExtension().webConfiguration.webSourcesDir}' 
        (!) directory '${projectExtension().webConfiguration.webBuildDir}' applied as resourcesDir 
        (!) buildWeb depends on prepareWeb
        (!) jar depends on buildWeb
        (!) clean depends on cleanWeb
        (!) build depends on buildWeb
    """.replaceIndent(ADDITIONAL_LOGGING_MESSAGE_INDENT), PURPLE_BOLD))
}