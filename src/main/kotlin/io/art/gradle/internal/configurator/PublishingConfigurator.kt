package io.art.gradle.internal.configurator

import io.art.gradle.common.constants.DOT_JAR
import io.art.gradle.common.constants.JAVA
import io.art.gradle.internal.constants.*
import io.art.gradle.internal.plugin.jvmPlugin
import io.art.gradle.internal.service.loadProperty
import io.art.gradle.internal.service.publishingRepositoryUrl
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.the

fun Project.configurePublishing() {
    val publisherUsername = loadProperty(PUBLISHER_USERNAME, PUBLISHING_PROPERTIES) ?: return
    val publisherPassword = loadProperty(PUBLISHER_PASSWORD, PUBLISHING_PROPERTIES) ?: return
    when (rootProject.name) {
        ART_JAVA -> mavenPublishing(publisherUsername, publisherPassword)
        ART_GENERATOR -> generatorPublishing(publisherUsername, publisherPassword)
        ART_KOTLIN -> mavenPublishing(publisherUsername, publisherPassword)
        ART_EXAMPLE -> mavenPublishing(publisherUsername, publisherPassword)
        ART_FIBERS -> mavenPublishing(publisherUsername, publisherPassword)
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
                        version = rootProject.version as String
                        from(project.components[JAVA])
                        projectPom(this)
                    }
                }
            }
        }
    }
}

private fun Project.projectPom(mavenPublication: MavenPublication) {
    mavenPublication.pom {
        name.set(project.name)
        url.set(gitUrl)
        licenses { licenses(this) }
        developers { developers(this) }
        scm { scm(this) }
        mavenPublication.versionMapping { versionMapping(this) }
    }
}

private fun Project.generatorPublishing(publisherUsername: String, publisherPassword: String) {
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
        publications {
            afterEvaluate {
                create<MavenPublication>(project.name) {
                    withoutBuildIdentifier()
                    artifactId = project.name
                    groupId = rootProject.group as String
                    version = rootProject.version as String
                    artifact(jvmPlugin.executable.directory.resolve("$name$DOT_JAR").toFile())
                    projectPom(this)
                }
            }
        }
    }
}
