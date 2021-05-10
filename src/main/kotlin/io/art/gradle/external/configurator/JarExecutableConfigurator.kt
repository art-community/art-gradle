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
import org.gradle.api.tasks.JavaExec
import org.gradle.jvm.tasks.Jar
import java.lang.Boolean.TRUE
import kotlin.let
import kotlin.to
import kotlin.with


fun Project.configureJar() {
    with(externalPlugin.extension.executable) {
        tasks.findByPath(BUILD_EXECUTABLE_JAR_TASK)?.let { return }
        if (!nativeEnabled && !jarEnabled) return

        mainClass ?: return

        val buildJar = tasks.register(BUILD_EXECUTABLE_JAR_TASK, Jar::class.java) {
            val jarTask = tasks.getByName(JAR)
            val compileTasks = tasks.filter { task -> task.name.startsWith(COMPILE_PREFIX) }
            val processResourcesTask = tasks.findByPath(PROCESS_RESOURCES) ?: return@register
            dependsOn(jarTask, compileTasks, processResourcesTask)

            group = ART

            isZip64 = true

            duplicatesStrategy = jar.classedDuplicateStrategy

            manifest {
                attributes(mapOf(MAIN_CLASS_MANIFEST_ATTRIBUTE to mainClass))
                if (jar.multiRelease && JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_1_9)) {
                    attributes(mapOf(MULTI_RELEASE_MANIFEST_ATTRIBUTE to TRUE.toString()))
                }
                attributes(jar.manifestAdditionalAttributes)
            }

            from(jarTask.outputs.files.map { if (it.isDirectory) it else zipTree(it) })
            from(configurations.getByName(EMBEDDED_CONFIGURATION_NAME).map { if (it.isDirectory) it else zipTree(it) })
            exclude(jar.exclusions)
            destinationDirectory.set(directory.toFile())

            archiveFileName.set("${this@with.executableName}.${archiveExtension.get()}")

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
