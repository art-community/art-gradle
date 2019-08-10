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

import org.gradle.api.Project
import org.gradle.api.tasks.compile.*
import org.gradle.plugins.ide.idea.model.*
import ru.art.gradle.constants.DefaultTasks.COMPILE_JAVA
import ru.art.gradle.constants.DefaultTasks.COMPILE_TEST_JAVA
import ru.art.gradle.constants.DependencyConfiguration.*
import ru.art.gradle.constants.IdeaScopeOperations.PLUS
import ru.art.gradle.constants.IdeaScopes.COMPILE
import ru.art.gradle.context.Context.projectExtension


fun Project.configureIdea() = extensions.configure(IdeaModel::class.java) { model ->
    model.module { module ->
        with(module) {
            inheritOutputDirs = false
            outputDir = (tasks.getByPath(COMPILE_JAVA) as JavaCompile).destinationDir
            testOutputDir = (tasks.getByPath(COMPILE_TEST_JAVA) as JavaCompile).destinationDir
            resourceDirs = projectExtension().resourcesConfiguration.resourceDirs.map { file(it) }.toSet()
            testResourceDirs = projectExtension().resourcesConfiguration.testResourceDirs.map { file(it) }.toSet()
            scopes[COMPILE]?.get(PLUS)?.addAll(listOf(configurations.getByName(EMBEDDED.configuration), configurations.getByName(PROVIDED.configuration)))
        }
    }
}