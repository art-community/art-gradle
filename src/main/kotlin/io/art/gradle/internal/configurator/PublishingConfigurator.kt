package io.art.gradle.internal.configurator

import io.art.gradle.internal.constants.*
import io.art.gradle.internal.service.loadProperties
import io.art.gradle.internal.service.publishingRepositoryUrl
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.the

fun Project.configurePublishing() {
    val publishingProperties = loadProperties(PUBLISHING_PROPERTIES)
    val publisherUsername = publishingProperties[PUBLISHER_USERNAME] ?: return
    val publisherPassword = publishingProperties[PUBLISHER_PASSWORD] ?: return
    when (rootProject.name) {
        ART_JAVA -> mavenPublishing(publisherUsername, publisherPassword)
        ART_GENERATOR -> mavenPublishing(publisherUsername, publisherPassword)
        ART_KOTLIN -> mavenPublishing(publisherUsername, publisherPassword)
    }
}

private fun Project.mavenPublishing(publisherUsername: String, publisherPassword: String) {
    plugins.apply(MavenPublishPlugin::class.java)
    with(the<PublishingExtension>()) {
        repositories {
            maven {
                url = uri(publishingRepositoryUrl)
                credentials {
                    username = publisherUsername
                    password = publisherPassword
                }
            }
        }
        subprojects {
            publications {
                afterEvaluate {
                    create<MavenPublication>(project.name) {
                        withoutBuildIdentifier()
                        artifactId = project.name
                        groupId = rootProject.group as String
                        version = resolveVersion()
                        from(project.components[JAVA])
                        pom {
                            name.set(project.name)
                            url.set(gitUrl)
                            licenses { licenses(this) }
                            developers { developers(this) }
                            scm { scm(this) }
                            versionMapping { versionMapping(this) }
                        }
                    }
                }
            }
        }
    }
}
