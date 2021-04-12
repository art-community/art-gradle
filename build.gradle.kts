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
    gradleVersion = "7.0-rc-2"
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
            implementationClass = "io.art.gradle.internal.InternalPlugin"
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

if (projectDir.resolve("local.properties").exists()) publishing {
    repositories {
        maven {
            url = uri("https://nexus.art-platform.io/repository/art-gradle-plugins/")
            credentials {
                val properties = Properties().apply { load(projectDir.resolve("local.properties").inputStream()) }
                username = properties["publisher.username"] as String
                password = properties["publisher.password"] as String
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
                url.set("https://github.com/art-community/${project.name}")
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
                    connection.set("scm:git:git://github.com/art-community/${project.name}.git")
                    developerConnection.set("scm:git:ssh://github.com/art-community/${project.name}.git")
                    url.set("https://github.com/art-community/${project.name}")
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
