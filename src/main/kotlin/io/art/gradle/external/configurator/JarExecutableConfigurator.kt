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

package io.art.gradle.external.configurator

import io.art.gradle.common.constants.ART
import io.art.gradle.external.constants.*
import io.art.gradle.external.plugin.externalPlugin
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.tasks.JavaExec
import org.gradle.jvm.tasks.Jar
import java.lang.Boolean.TRUE
import kotlin.with


fun Project.configureJar() {
    with(externalPlugin.extension.executable) {
        tasks.findByPath(BUILD_EXECUTABLE_JAR_TASK)?.let { return }
        if (!nativeEnabled && !jarEnabled) return

        mainClass ?: return

        val buildJar = tasks.register(BUILD_EXECUTABLE_JAR_TASK, Jar::class.java) {
            val jarTask = tasks.getByName(JAR)
            val embedded = configurations.getByName(EMBEDDED_CONFIGURATION_NAME)

            embedded.incoming.resolutionResult.allDependencies {
                if (from.id is ProjectComponentIdentifier) {
                    val id = from.id as ProjectComponentIdentifier
                    gradle.includedBuilds
                            .filter { build -> id.build.name == build.name }
                            .forEach { build -> dependsOn(build.task(":${id.projectName}:${JAR}")) }
                    rootProject
                            .subprojects
                            .filter { subProject -> id.build.isCurrentBuild && subProject.name == id.projectName }
                            .forEach { subProject -> dependsOn(":${subProject.name}:$JAR") }
                }
            }

            dependsOn(jarTask)

            if (jar.asBuildDependency) {
                tasks.getByPath(BUILD).dependsOn(BUILD_EXECUTABLE_JAR_TASK)
            }

            group = ART

            isZip64 = true

            duplicatesStrategy = jar.duplicateStrategy

            manifest {
                attributes(mapOf(MAIN_CLASS_MANIFEST_ATTRIBUTE to mainClass))
                if (jar.multiRelease && JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_1_9)) {
                    attributes(mapOf(MULTI_RELEASE_MANIFEST_ATTRIBUTE to TRUE.toString()))
                }
                attributes(jar.manifestAdditionalAttributes)
            }

            from(jarTask.outputs.files.map { if (it.isDirectory) it else zipTree(it) })

            from(embedded.map { if (it.isDirectory) it else zipTree(it) })

            exclude(jar.exclusions)

            destinationDirectory.set(directory.toFile())

            archiveFileName.set("$executableName.${archiveExtension.get()}")

            jar.buildConfigurator(this)
        }

        tasks.findByPath(RUN_EXECUTABLE_JAR_TASK)?.let { return }

        tasks.register(RUN_EXECUTABLE_JAR_TASK, JavaExec::class.java) {
            dependsOn(buildJar)
            classpath(buildJar.get().outputs.files)
            mainClass.set(this@with.mainClass)
            group = ART
            jar.runConfigurator(this)
        }
    }
}
