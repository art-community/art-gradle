package ru.art.configurator.project

import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.plugins.ide.idea.model.IdeaModel
import ru.art.constants.DefaultTasks.COMPILE_JAVA
import ru.art.constants.DefaultTasks.COMPILE_TEST_JAVA
import ru.art.constants.DependencyConfiguration.EMBEDDED
import ru.art.constants.DependencyConfiguration.PROVIDED
import ru.art.constants.IdeaScopeOperations.PLUS
import ru.art.constants.IdeaScopes.COMPILE
import ru.art.context.Context.projectConfiguration


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