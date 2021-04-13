/*
 * ART
 *
 * Copyright 2020 ART
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.*

plugins {
    `kotlin-dsl`
    `java-library`
    `maven-publish`
}

group = "io.art.gradle"
version = "main"

tasks.withType(type = Wrapper::class) {
    gradleVersion = "7.0"
}

repositories {
    mavenCentral()
}

gradlePlugin {
    isAutomatedPublishing = false
    plugins {
        create("java-generator") {
            id = "java-generator"
            implementationClass = "io.art.gradle.external.JavaGeneratorPlugin"
        }
        create("art-internal") {
            id = "art-internal"
            implementationClass = "io.art.gradle.internal.plugin.InternalPlugin"
        }
        create("kotlin-generator") {
            id = "kotlin-generator"
            implementationClass = "io.art.gradle.external.KotlinGeneratorPlugin"
        }
    }
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("org.eclipse.jgit:org.eclipse.jgit:+")
}

fun configurePublishing(publishingUsername: String, publishingPassword: String) = publishing {
    val communityRepository = "github.com/art-community"
    val communityUrl = "https://$communityRepository/${project.name}"

    repositories {
        maven {
            url = uri("https://nexus.art-platform.io/repository/art-gradle-plugins/")
            credentials {
                username = publishingUsername
                password = publishingPassword
            }
        }
    }

    publications {
        create<MavenPublication>(project.name) {
            withoutBuildIdentifier()
            artifactId = project.name
            groupId = project.group as String
            from(project.components["java"])
            suppressAllPomMetadataWarnings()
            pom {
                name.set(project.name)
                url.set(communityUrl)

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
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
                    connection.set("scm:git:git://$communityRepository/${project.name}.git")
                    developerConnection.set("scm:git:ssh://$communityRepository/${project.name}.git")
                    url.set(communityUrl)
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

val publishingProperties
    get(): Map<String, String> {
        val propertiesName = "publishing.properties"
        val content = properties[propertiesName] as String?
                ?: rootDir.parentFile?.resolve(propertiesName)?.readText()
                ?: return emptyMap()
        return Properties()
                .apply { load(content.reader()) }
                .entries
                .associate { entry -> "${entry.key}" to "${entry.value}" }
    }

val publishingUsername = publishingProperties["publisher.username"]
val publishingPassword = publishingProperties["publisher.password"]

if (!publishingUsername.isNullOrBlank() && !publishingPassword.isNullOrBlank()) {
    configurePublishing(publishingUsername, publishingPassword)
}
