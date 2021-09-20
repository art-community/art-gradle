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

package io.art.gradle.common.configurator

import io.art.gradle.common.configuration.JarExecutableConfiguration
import io.art.gradle.common.constants.*
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.tasks.JavaExec
import org.gradle.jvm.tasks.Jar
import java.lang.Boolean.TRUE
import java.nio.file.Path


data class JarExecutableCreationConfiguration(
        val configuration: JarExecutableConfiguration,
        val runTask: String,
        val buildTask: String,
        val dependencyConfiguration: String,
        val mainClass: String?,
        val executable: String,
        val directory: Path,
        val configurator: Jar.() -> Unit = { },
)

fun Project.configureJar(configuration: JarExecutableCreationConfiguration) {
    val jar = configuration.configuration
    tasks.findByPath(configuration.buildTask)?.let { return }

    val buildJar = tasks.register(configuration.buildTask, Jar::class.java) {
        val jarTask = tasks.getByName(JAR)
        val embedded = configurations.getByName(configuration.dependencyConfiguration)

        addGradleBuildDependencies(embedded, this)

        dependsOn(jarTask)

        if (jar.asBuildDependency) {
            tasks.getByPath(BUILD).dependsOn(configuration.buildTask)
        }

        group = ART

        isZip64 = true

        duplicatesStrategy = jar.duplicateStrategy

        val attributes: MutableMap<String, String> = mutableMapOf()
        configuration.mainClass?.let { main -> attributes += MAIN_CLASS_MANIFEST_ATTRIBUTE to main }
        if (jar.multiRelease && JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_1_9)) {
            attributes[MULTI_RELEASE_MANIFEST_ATTRIBUTE] = TRUE.toString()
        }
        attributes += jar.manifestAttributes

        manifest {
            attributes(jar.manifestAttributesReplacer(attributes))
        }

        from(jarTask.outputs.files.map { if (it.isDirectory) it else zipTree(it) })

        from(embedded.map { if (it.isDirectory) it else zipTree(it) })

        exclude(jar.exclusions)

        destinationDirectory.set(configuration.directory.toFile())

        archiveFileName.set("${configuration.executable}.${archiveExtension.get()}")

        configuration.configurator(this)

        jar.buildConfigurator(this)
    }

    tasks.findByPath(configuration.runTask)?.let { return }

    tasks.register(configuration.runTask, JavaExec::class.java) {
        dependsOn(buildJar)
        classpath(buildJar.get().outputs.files)
        configuration.mainClass?.let(mainClass::set)
        group = ART
        jar.runConfigurator(this)
    }
}

private fun Project.addGradleBuildDependencies(configuration: Configuration, jar: Jar) {
    configuration.incoming.resolutionResult.allDependencies {
        if (from.id is ProjectComponentIdentifier) {
            val id = from.id as ProjectComponentIdentifier
            project.gradle.includedBuilds
                    .filter { build -> id.build.name == build.name }
                    .forEach { build -> jar.dependsOn(build.task(":${id.projectName}:$JAR")) }
            project.rootProject
                    .subprojects
                    .filter { subProject -> id.build.isCurrentBuild && subProject.name == id.projectName }
                    .forEach { subProject -> jar.dependsOn(":${subProject.name}:$JAR") }
        }
    }
}
