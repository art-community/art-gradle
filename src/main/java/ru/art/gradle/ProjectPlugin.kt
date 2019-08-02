package ru.art.gradle

import org.gradle.api.*
import ru.art.gradle.configuration.*
import ru.art.gradle.configurator.project.*
import ru.art.gradle.constants.*
import ru.art.gradle.context.Context.setProjectContext
import ru.art.gradle.git.*
import ru.art.gradle.logging.*


open class ProjectPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        attention("Start of ART project configuring")
        setProjectContext(extensions.create(ART_EXTENSION, ProjectConfiguration::class.java, this), loadGitRepository())
        configureProject()
    }
}