package ru.adk.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.adk.gradle.plugin.configuration.ProjectConfiguration
import ru.adk.gradle.plugin.configurator.project.configureProject
import ru.adk.gradle.plugin.constants.ADK_EXTENSION
import ru.adk.gradle.plugin.context.Context.setProjectContext
import ru.adk.gradle.plugin.git.loadGitRepository
import ru.adk.gradle.plugin.logging.attention


open class ProjectPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        attention("Start of ADK project configuring")
        setProjectContext(extensions.create(ADK_EXTENSION, ProjectConfiguration::class.java, this), loadGitRepository())
        configureProject()
    }
}