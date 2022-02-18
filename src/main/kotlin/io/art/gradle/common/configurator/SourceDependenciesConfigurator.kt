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

import io.art.gradle.common.constants.BUILD
import io.art.gradle.common.constants.MAKE_FILE
import io.art.gradle.common.logger.logger
import io.art.gradle.external.plugin.externalPlugin
import org.eclipse.jgit.api.Git
import org.gradle.api.Project

fun Project.configureSourceDependencies() {
    val logger = logger(project.name)
    val sources = externalPlugin.configuration.sources
    sources.unixDependencies.forEach { dependency ->
        tasks.register("$BUILD-${dependency.name}") {
            group = BUILD
            doLast {
                if (!sources.directory.toFile().exists()) {
                    Git.cloneRepository()
                            .setDirectory(sources.directory.resolve(dependency.name).toFile())
                            .setBare(true)
                            .setURI(dependency.url!!)
                            .call()
                }
                exec {
                    when (sources.directory.resolve(dependency.name).resolve(MAKE_FILE).toFile().exists()) {
                        true -> commandLine(*dependency.makeCommand())
                        false -> commandLine(*dependency.fullCommand())
                    }
                    workingDir(sources.directory.resolve(dependency.name))
                    standardOutput = logger.output()
                    errorOutput = logger.error()
                }
            }
        }
    }
}
