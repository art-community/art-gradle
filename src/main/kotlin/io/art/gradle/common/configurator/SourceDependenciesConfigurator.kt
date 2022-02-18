/*
 * ART
 *
 * Copyright 2019-2022 ART
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

package io.art.gradle.common.configurator

import SourceDependenciesConfiguration
import UnixSourceDependency
import io.art.gradle.common.constants.*
import io.art.gradle.common.logger.logger
import io.art.gradle.external.plugin.externalPlugin
import org.eclipse.jgit.api.Git
import org.gradle.api.Project
import java.io.File

fun Project.configureSourceDependencies() {
    val sources = externalPlugin.configuration.sources
    sources.unixDependencies.forEach { dependency -> configureUnix(dependency, sources) }
}

private fun Project.configureUnix(dependency: UnixSourceDependency, sources: SourceDependenciesConfiguration) {
    val logger = logger(project.name)
    tasks.register("$BUILD-${dependency.name}") {
        group = BUILD
        doLast {
            val dependencyDirectory = sources.directory.resolve(dependency.name).toFile()
            if (!dependencyDirectory.exists()) {
                dependencyDirectory.mkdirs()
                Git.cloneRepository()
                        .setDirectory(dependencyDirectory)
                        .setURI(dependency.url!!)
                        .setCloneAllBranches(true)
                        .setCloneSubmodules(true)
                        .call()
                        .fetch()
                        .call()
            }

            if (dependencyDirectory.resolve(MAKE_FILE).exists()) {
                if (File(DOS_TO_UNIX_FILE).exists()) {
                    dos2Unix(dependencyDirectory, dependencyDirectory.resolve(MAKE_FILE))
                }
                executeDependencyCommand(dependency.makeCommand(), dependencyDirectory)
                return@doLast
            }

            if (dependencyDirectory.resolve(CONFIGURE_SCRIPT).exists()) {
                if (File(DOS_TO_UNIX_FILE).exists()) {
                    dos2Unix(dependencyDirectory, dependencyDirectory.resolve(CONFIGURE_FILE))
                }
                executeDependencyCommand(dependency.configureCommand(), dependencyDirectory)
                executeDependencyCommand(dependency.makeCommand(), dependencyDirectory)
                return@doLast
            }

            if (File(DOS_TO_UNIX_FILE).exists()) {
                dos2Unix(dependencyDirectory, dependencyDirectory.resolve(AUTOGEN_FILE))
            }

            executeDependencyCommand(dependency.autogenCommand(), dependencyDirectory)
            executeDependencyCommand(dependency.configureCommand(), dependencyDirectory)
            executeDependencyCommand(dependency.makeCommand(), dependencyDirectory)
        }
    }
}

private fun Project.dos2Unix(dependencyDirectory: File, file: File) {
    val logger = logger(project.name)
    exec {
        commandLine(DOS_TO_UNIX_FILE, file)
        workingDir(dependencyDirectory)
        errorOutput = logger.error()
    }
}

private fun Project.executeDependencyCommand(command: Array<String>, dependencyDirectory: File) {
    val logger = logger(project.name)
    exec {
        commandLine(*command)
        workingDir(dependencyDirectory)
        errorOutput = logger.error()
    }
}
