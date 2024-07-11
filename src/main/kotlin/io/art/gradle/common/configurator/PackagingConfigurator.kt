/*
 * ART
 *
 * Copyright 2019-2022 ART
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

import io.art.gradle.common.configuration.PackagingConfiguration
import io.art.gradle.common.constants.*
import io.art.gradle.common.service.DownloadingRequest
import io.art.gradle.common.service.FileDownloadService
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.initialization.IncludedBuild
import org.gradle.api.internal.artifacts.ForeignBuildIdentifier
import org.gradle.internal.jvm.Jvm
import org.gradle.internal.os.OperatingSystem.current
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.support.unzipTo
import org.gradle.kotlin.dsl.support.zipTo
import java.io.File
import java.lang.Boolean.TRUE
import java.net.URI
import java.nio.file.Path


data class PackagingCreationConfiguration(
    val configuration: PackagingConfiguration,
    val buildTask: String,
    val dependencyConfiguration: String,
    val mainClass: String?,
    val executable: String,
    val directory: Path,
    val configurator: Jar.() -> Unit = { },
)

fun Project.configurePackaging(configuration: PackagingCreationConfiguration) {
    val jar = configuration.configuration
    tasks.findByPath(configuration.buildTask)?.let { return }
    tasks.register(configuration.buildTask, Jar::class.java) {
        val jarTask = tasks.getByName(JAR)
        val embedded = configurations.getByName(configuration.dependencyConfiguration)

        addGradleBuildDependencies(embedded, this)

        dependsOn(jarTask)

        group = PACKAGE

        isZip64 = true

        duplicatesStrategy = jar.duplicateStrategy

        val attributes: MutableMap<String, String> = mutableMapOf()
        configuration.mainClass?.let { main -> attributes += MAIN_CLASS_MANIFEST_ATTRIBUTE to main }
        attributes[MULTI_RELEASE_MANIFEST_ATTRIBUTE] = TRUE.toString()
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

        doLast {
            createPackage(configuration, this@register)
        }
    }
}

private fun Project.createPackage(configuration: PackagingCreationConfiguration, jar: Jar) {
    val output = configuration.directory.resolve(PACKAGE).toFile()
    val jre = configuration.directory.resolve(JRE).toFile()
    val jreArchive = jre.resolve(configuration.configuration.jreUrl.substringAfterLast(SLASH)).toPath()
    var runtime = jre.resolve(RUNTIME)
    if (output.exists()) output.deleteRecursively()
    if (!runtime.exists()) {
        if (jre.exists()) jre.deleteRecursively()
        runtime.mkdirs()
        val request = DownloadingRequest(
            url = URI(configuration.configuration.jreUrl),
            path = jreArchive,
            lockName = "$PACKAGE$DOT_LOCK",
            timeout = PACKAGE_JRE_DOWNLOAD_TIMEOUT
        )
        FileDownloadService.downloadFile(request)
        copy {
            from(
                when {
                    current().isWindows -> zipTree(jreArchive)
                    else -> tarTree(jreArchive)
                }
            )
            into(runtime)
        }
    }
    runtime = runtime.listFiles()!!.first()
    exec {
        commandLine(runtime.resolve(BIN).resolve(JLINK))
        args(
            *JLINK_OPTIONS(runtime),
            output.absolutePath,
        )
    }
    copy {
        from(jar.archiveFile.get().asFile.absolutePath)
        into(output)
    }
    zipTo(output.parentFile.resolve(configuration.executable + DOT + ZIP), output)
}


private fun Project.addGradleBuildDependencies(configuration: Configuration, jar: Jar) {
    configuration.incoming.resolutionResult.allDependencies {
        if (from.id is ProjectComponentIdentifier) {
            val dependencyId = from.id as ProjectComponentIdentifier

            val builds = mutableListOf<IncludedBuild>()
            var current = gradle
            while (true) {
                current.includedBuilds.forEach(builds::add)
                current.parent ?: break
                current = current.parent!!
            }

            builds.filter { build -> dependencyId.build.buildPath == build.name && dependencyId.build !is ForeignBuildIdentifier }.forEach { build ->
                jar.dependsOn(build.task(":${dependencyId.projectName}:$JAR"))
            }

            project.rootProject
                .subprojects
                .filter { subProject -> dependencyId.build is ForeignBuildIdentifier && subProject.name == dependencyId.projectName }
                .forEach { subProject -> jar.dependsOn(":${subProject.name}:$JAR") }
        }
    }
}
