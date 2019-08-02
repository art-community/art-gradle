package ru.art.gradle.configurator.project

import org.gradle.api.*
import org.gradle.api.artifacts.maven.Conf2ScopeMappingContainer.*
import org.gradle.api.plugins.*
import org.gradle.api.plugins.MavenPlugin.*
import org.gradle.kotlin.dsl.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.DefaultTasks.BUILD
import ru.art.gradle.context.Context.projectConfiguration
import ru.art.gradle.logging.*
import ru.art.gradle.logging.LogMessageColor.*
import ru.art.gradle.provider.*

fun Project.configurePublishing() {
    val url = URL_PARAMETER to uri(projectConfiguration().publishingConfiguration.getUrlParameter(this@configurePublishing) + SLASH + projectConfiguration().publishingConfiguration.repositoryId)
    val username = USERNAME_PARAMETER to projectConfiguration().publishingConfiguration.getUsernameParameter(this@configurePublishing)
    val password = PASSWORD_PARAMETER to projectConfiguration().publishingConfiguration.getPasswordParameter(this@configurePublishing)
    convention.getPlugin(MavenPluginConvention::class.java)
            .conf2ScopeMappings
            .addMapping(PROVIDED_RUNTIME_PRIORITY, configurations.getAt(DependencyConfiguration.EMBEDDED.configuration), PROVIDED)
    with(uploadTask()) {
        repositories.withGroovyBuilder {
            MAVEN_DEPLOYER {
                REPOSITORY(url) { AUTHENTICATION(username, password) }
            }
        }
        dependsOn(BUILD)
        doLast {
            success("Uploading artifact '${jarTask().archiveBaseName.get()}' to ${url.second}/${(project.group as String).replace(DOT, SLASH)}/${jarTask().archiveBaseName.get()}/$version")
        }
    }

    success("Configured uploadArchives (depends on build task) task. Upload will be processed to repository with parameters:\n" + message("""
    URL = ${url.second}
    Username = ${username.second}
    Password = ${EMPTY_STRING.padStart(password.second.length, STAR)}
    """.replaceIndent(ADDITIONAL_LOGGING_MESSAGE_INDENT), PURPLE_BOLD))
}
