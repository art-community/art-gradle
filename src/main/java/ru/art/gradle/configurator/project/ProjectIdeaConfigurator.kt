package ru.art.gradle.configurator.project

import org.gradle.api.Project
import org.gradle.api.tasks.compile.*
import org.gradle.plugins.ide.idea.model.*
import ru.art.gradle.constants.DefaultTasks.COMPILE_JAVA
import ru.art.gradle.constants.DefaultTasks.COMPILE_TEST_JAVA
import ru.art.gradle.constants.DependencyConfiguration.*
import ru.art.gradle.constants.IdeaScopeOperations.PLUS
import ru.art.gradle.constants.IdeaScopes.COMPILE
import ru.art.gradle.context.Context.projectConfiguration


fun Project.configureIdea() = extensions.configure(IdeaModel::class.java) { model ->
    model.module { module ->
        with(module) {
            inheritOutputDirs = false
            outputDir = (tasks.getByPath(COMPILE_JAVA) as JavaCompile).destinationDir
            testOutputDir = (tasks.getByPath(COMPILE_TEST_JAVA) as JavaCompile).destinationDir
            resourceDirs = projectConfiguration().resourcesConfiguration.resourceDirs.map { file(it) }.toSet()
            testResourceDirs = projectConfiguration().resourcesConfiguration.testResourceDirs.map { file(it) }.toSet()
            scopes[COMPILE]?.get(PLUS)?.addAll(listOf(configurations.getByName(EMBEDDED.configuration), configurations.getByName(PROVIDED.configuration)))
        }
    }
}