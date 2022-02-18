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

import io.art.gradle.common.constants.AUTOGEN_FILE
import io.art.gradle.common.constants.BUILD
import io.art.gradle.common.constants.DOS_TO_UNIX_FILE
import io.art.gradle.common.constants.MAKE_FILE
import io.art.gradle.common.logger.logger
import io.art.gradle.external.plugin.externalPlugin
import org.eclipse.jgit.api.Git
import org.gradle.api.Project
import java.io.File

fun Project.configureSourceDependencies() {
    val logger = logger(project.name)
    val sources = externalPlugin.configuration.sources
    sources.unixDependencies.forEach { dependency ->
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
                when (dependencyDirectory.resolve(MAKE_FILE).exists()) {
                    true -> exec {
                        commandLine(dependency.makeCommand())
                        workingDir(sources.directory.resolve(dependency.name))
                        standardOutput = logger.output()
                        errorOutput = logger.error()
                    }
                    false -> {
                        if (File(DOS_TO_UNIX_FILE).exists()) {
                            exec {
                                commandLine(DOS_TO_UNIX_FILE, dependencyDirectory.resolve(AUTOGEN_FILE))
                                workingDir(dependencyDirectory)
                                standardOutput = logger.output()
                                errorOutput = logger.error()
                            }
                        }
                        dependency.fullCommands().forEach { command ->
                            exec {
                                commandLine(*command)
                                workingDir(dependencyDirectory)
                                standardOutput = logger.output()
                                errorOutput = logger.error()
                            }
                        }
                    }
                }
            }
        }
    }
}
