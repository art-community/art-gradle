package ru.art

import org.gradle.api.*
import ru.art.configuration.*
import ru.art.configurator.project.*
import ru.art.constants.*
import ru.art.context.Context.setProjectContext
import ru.art.git.*
import ru.art.logging.*


open class ProjectPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        attention("Start of ART project configuring")
        setProjectContext(extensions.create(ART_EXTENSION, ProjectConfiguration::class.java, this), loadGitRepository())
        configureProject()
    }
}