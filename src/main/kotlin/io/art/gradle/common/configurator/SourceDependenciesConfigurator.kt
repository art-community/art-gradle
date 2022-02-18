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
import org.eclipse.jgit.api.Git
import org.gradle.api.Project
import java.io.File

fun Project.configureSourceDependencies(configuration: SourceDependenciesConfiguration) {
    configuration.unixDependencies.forEach { dependency -> configureUnix(dependency, configuration) }
}

private fun Project.configureUnix(dependency: UnixSourceDependency, sources: SourceDependenciesConfiguration) {
    tasks.register("$BUILD-${dependency.name}") {
        group = BUILD
        if (dependency.buildDependency) tasks.findByPath(BUILD)?.dependsOn(this)
        doLast {
            val dependencyDirectory = sources.directory.resolve(dependency.name).toFile()
            if (!dependencyDirectory.exists()) {
                dependencyDirectory.mkdirs()
                Git.cloneRepository()
                        .setDirectory(dependencyDirectory)
                        .setURI(dependency.url!!)
                        .setCloneSubmodules(true)
                        .call()
            }

            if (dependencyDirectory.resolve(MAKE_FILE).exists()) {
                executeDependencyCommand(dependency.makeCommand(), dependencyDirectory)
                copyDependencyBuiltFiles(dependency, dependencyDirectory)
                return@doLast
            }

            if (dependencyDirectory.resolve(CONFIGURE_SCRIPT).exists()) {
                dos2Unix(dependencyDirectory)
                executeDependencyCommand(dependency.configureCommand(), dependencyDirectory)
                executeDependencyCommand(dependency.makeCommand(), dependencyDirectory)
                copyDependencyBuiltFiles(dependency, dependencyDirectory)
                return@doLast
            }

            dos2Unix(dependencyDirectory)
            executeDependencyCommand(dependency.autogenCommand(), dependencyDirectory)
            executeDependencyCommand(dependency.configureCommand(), dependencyDirectory)
            executeDependencyCommand(dependency.makeCommand(), dependencyDirectory)
            copyDependencyBuiltFiles(dependency, dependencyDirectory)
        }
    }
}

private fun Project.copyDependencyBuiltFiles(dependency: UnixSourceDependency, dependencyDirectory: File) {
    dependency.builtFiles().forEach { (from, to) ->
        copy {
            from(dependencyDirectory.resolve(from).absolutePath)
            into(projectDir.resolve(to).absolutePath)
        }
    }
}

private fun Project.dos2Unix(dependencyDirectory: File) {
    val logger = logger(project.name)
    dependencyDirectory.listFiles()!!.asSequence().filter { file -> file.isFile }.forEach { file ->
        exec {
            commandLine(*bashCommand(DOS_TO_UNIX_FILE, file.absolutePath.wsl()))
            workingDir(dependencyDirectory)
            errorOutput = logger.error()
        }
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
