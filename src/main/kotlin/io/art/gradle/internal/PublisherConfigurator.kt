package io.art.gradle.internal

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.the
import java.util.*

fun Project.configurePublishing() {
    val propertiesFile = rootDir.parentFile?.resolve(PUBLISHING_PROPERTIES) ?: return
    if (!propertiesFile.exists()) return
    val commonProperties = Properties().apply { load(file(propertiesFile).inputStream()) }
    when (rootProject.name) {
        ART_JAVA -> javaPublishing(commonProperties[PUBLISHER_USERNAME] as String, commonProperties[PUBLISHER_PASSWORD] as String)
        ART_GENERATOR -> javaPublishing(commonProperties[PUBLISHER_USERNAME] as String, commonProperties[PUBLISHER_PASSWORD] as String)
        ART_KOTLIN -> javaPublishing(commonProperties[PUBLISHER_USERNAME] as String, commonProperties[PUBLISHER_PASSWORD] as String)
    }
}

private fun Project.javaPublishing(publisherUserName: String, publisherPassword: String) {
    plugins.apply("maven-publish")
    with(the<PublishingExtension>()) {
        repositories {
            maven {
                url = uri(artifactsRepositoryUrl)
                credentials {
                    username = publisherUserName
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
                        from(project.components["java"])
                        pom {
                            name.set(project.name)
                            url.set("https://github.com/art-community/${rootProject.name}")
                            licenses {
                                license {
                                    name.set("The Apache License, Version 2.0")
                                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                                    distribution.set("repo")
                                }
                            }
                            developers {
                                developer {
                                    id.set("anton.bashirov")
                                    name.set("Anton Bashirov")
                                    email.set("anton.sh.local@gmail.com")
                                }
                            }

                            scm {
                                connection.set("scm:git:git://github.com/art-community/${rootProject.name}.git")
                                developerConnection.set("scm:git:ssh://github.com/art-community/${rootProject.name}.git")
                                url.set("https://github.com/art-community/${rootProject.name}")
                            }

                            versionMapping {
                                usage("java-api") {
                                    fromResolutionResult()
                                }
                                usage("java-runtime") {
                                    fromResolutionResult()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
