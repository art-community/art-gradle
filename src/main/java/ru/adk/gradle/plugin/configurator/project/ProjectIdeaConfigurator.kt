package ru.adk.gradle.plugin.configurator.project

import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.plugins.ide.idea.model.IdeaModel
import ru.adk.gradle.plugin.constants.DefaultTasks.COMPILE_JAVA
import ru.adk.gradle.plugin.constants.DefaultTasks.COMPILE_TEST_JAVA
import ru.adk.gradle.plugin.constants.DependencyConfiguration.EMBEDDED
import ru.adk.gradle.plugin.constants.DependencyConfiguration.PROVIDED
import ru.adk.gradle.plugin.constants.IdeaScopeOperations.PLUS
import ru.adk.gradle.plugin.constants.IdeaScopes.COMPILE
import ru.adk.gradle.plugin.context.Context.projectConfiguration


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