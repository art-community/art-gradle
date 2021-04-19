package io.art.gradle.internal.constants

import org.gradle.api.Project
import org.gradle.api.publish.VersionMappingStrategy
import org.gradle.api.publish.maven.MavenPomDeveloperSpec
import org.gradle.api.publish.maven.MavenPomLicenseSpec
import org.gradle.api.publish.maven.MavenPomScm

const val PUBLISHING_PROPERTIES = "publishing.properties"
const val PUBLISHER_USERNAME = "publisher.username"
const val PUBLISHER_PASSWORD = "publisher.password"

fun Project.developers(spec: MavenPomDeveloperSpec) = with(spec) {
    developer {
        id.set("anton.bashirov")
        name.set("Anton Bashirov")
        email.set("anton.sh.local@gmail.com")
    }
}

fun Project.licenses(spec: MavenPomLicenseSpec) = with(spec) {
    license {
        name.set("The Apache License, Version 2.0")
        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
    }
}

fun Project.scm(spec: MavenPomScm) = with(spec) {
    connection.set("scm:git:git://$ART_COMMUNITY_REPOSITORY/${rootProject.name}.git")
    developerConnection.set("scm:git:ssh://$ART_COMMUNITY_REPOSITORY/${rootProject.name}.git")
    url.set(ART_COMMUNITY_URL)
}

fun Project.versionMapping(strategy: VersionMappingStrategy) = with(strategy) {
    allVariants {
        fromResolutionResult()
    }
}
