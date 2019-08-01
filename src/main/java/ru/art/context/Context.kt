package ru.art.context

import org.eclipse.jgit.api.Git
import org.gradle.api.Project
import ru.art.configuration.ProjectConfiguration
import ru.art.configuration.SettingsConfiguration
import java.util.concurrent.ConcurrentHashMap

object Context {
    val projectsContext = ConcurrentHashMap<String, ProjectContext>()
    @Volatile
    lateinit var settingsConfiguration: SettingsConfiguration

    fun Project.projectConfiguration() = projectsContext[name]!!.projectConfiguration

    fun Project.git() = projectsContext[name]!!.git

    fun Project.auxiliaryInformation() = projectsContext[name]!!.projectAuxiliaryInformation

    fun Project.setProjectContext(projectConfiguration: ProjectConfiguration, git: Git?) {
        projectsContext[name] = ProjectContext(projectConfiguration = projectConfiguration, git = git, projectAuxiliaryInformation = ProjectAuxiliaryInformation())
    }

    fun Project.setAfterConfiguringAction(action: (configuration: ProjectConfiguration) -> Unit) {
        projectsContext[name]?.afterConfiguringAction = action
    }

    fun Project.runAfterConfiguringAction() = projectsContext[name]?.afterConfiguringAction?.invoke(projectConfiguration())


    data class ProjectContext(val projectConfiguration: ProjectConfiguration,
                              val git: Git? = null,
                              val projectAuxiliaryInformation: ProjectAuxiliaryInformation,
                              var afterConfiguringAction: (configuration: ProjectConfiguration) -> Unit = {})

    data class ProjectAuxiliaryInformation(var hasGroovy: Boolean = false,
                                           var hasScala: Boolean = false,
                                           var hasKotlin: Boolean = false,
                                           var hasGatling: Boolean = false,
                                           var hasJmh: Boolean = false,
                                           var hasWeb: Boolean = false,
                                           var hasGroovyTests: Boolean = false,
                                           var hasScalaTests: Boolean = false,
                                           var hasKotlinTests: Boolean = false,
                                           var hasCheckstyle: Boolean = false,
                                           var hasProtobufGenerator: Boolean = false,
                                           var hasGenerator: Boolean = false,
                                           var hasLombok: Boolean = false,
                                           var hasSpock: Boolean = false
    )
}