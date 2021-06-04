/*
 * ART
 *
 * Copyright 2019-2021 ART
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
        create("art-jvm") {
            id = "art-jvm"
            implementationClass = "io.art.gradle.external.plugin.ExternalJvmPlugin"
        }
        create("art-internal-jvm") {
            id = "art-internal-jvm"
            implementationClass = "io.art.gradle.internal.plugin.InternalJvmPlugin"
        }
        create("art-internal-lua") {
            id = "art-internal-lua"
            implementationClass = "io.art.gradle.internal.plugin.InternalLuaPlugin"
        }
    }
}

dependencies {
    implementation("org.eclipse.jgit:org.eclipse.jgit:+")
    implementation("org.yaml:snakeyaml:+")
}

fun configurePublishing(publisherUsername: String, publisherPassword: String) = publishing {
    val communityRepository = "github.com/art-community"
    val communityUrl = "https://$communityRepository/${project.name}"

    repositories {
        maven {
            url = uri("https://nexus.art-platform.io/repository/art-gradle-plugins/")
            credentials {
                username = publisherUsername
                password = publisherPassword
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
                    allVariants {
                        fromResolutionResult()
                    }
                }
            }
        }
    }
}

val publishingProperties
    get(): Map<String, String> {
        val content = rootDir.parentFile
                ?.resolve("publishing.properties")
                ?.takeIf(File::exists)
                ?.readText()
                ?: return emptyMap()
        return Properties()
                .apply { load(content.reader()) }
                .entries
                .associate { entry -> "${entry.key}" to "${entry.value}" }
    }

val userNameProperty = "publisher.username"
val passwordProperty = "publisher.password"
val publisherUsername = publishingProperties[userNameProperty] ?: properties[userNameProperty]?.toString()
val publisherPassword = publishingProperties[passwordProperty] ?: properties[passwordProperty]?.toString()

if (!publisherUsername.isNullOrBlank() && !publisherPassword.isNullOrBlank()) {
    configurePublishing(publisherUsername, publisherPassword)
}
