/*
 * ART Java
 *
 * Copyright 2019 ART
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

package ru.art.gradle.configurator.project

import org.gradle.api.*
import org.gradle.api.file.DuplicatesStrategy.*
import org.gradle.api.plugins.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.utils.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.DependencyConfiguration.*
import ru.art.gradle.constants.GradleVersion.*
import ru.art.gradle.context.Context.projectExtension
import ru.art.gradle.logging.*
import ru.art.gradle.provider.*

fun Project.configureJava() {
    val compileJava = compileJavaTask()
    val compileTestJava = compileTestJavaTask()

    if (isGradleVersionAtLeast(GRADLE_4_6.major, GRADLE_4_6.minor)) {
        compileJava
                .options
                .annotationProcessorPath = files(configurations[ANNOTATION_PROCESSOR.configuration].files)
    }

    if (isGradleVersionAtLeast(GRADLE_4_6.major, GRADLE_4_6.minor)) {
        compileTestJava
                .options
                .annotationProcessorPath = files(configurations[ANNOTATION_PROCESSOR.configuration].files)
    }

    with(convention.getPlugin(JavaPluginConvention::class.java)) {
        val mainSourceSet = sourceSets[MAIN_SOURCE_SET]
        val testSourceSet = sourceSets[TEST_SOURCE_SET]

        mainSourceSet.resources.setSrcDirs(projectExtension().resourcesConfiguration.resourceDirs)
        testSourceSet.resources.setSrcDirs(projectExtension().resourcesConfiguration.testResourceDirs)

        with(compileJavaTask()) {
            doLast {
                projectExtension().resourcesConfiguration.resourceDirs.forEach { dir ->
                    copy { copy ->
                        with(copy) {
                            from(dir)
                            into(mainSourceSet.java.outputDir)
                        }
                    }
                }
                projectExtension().resourcesConfiguration.testResourceDirs.forEach { dir ->
                    copy { copy ->
                        with(copy) {
                            from(dir)
                            into(testSourceSet.java.outputDir)
                        }
                    }
                }
            }
        }

        with(jarTask()) {
            isZip64 = true
            duplicatesStrategy = EXCLUDE

            mainSourceSet.output.classesDirs.forEach { classpathSource -> from(classpathSource) }
            configurations[EMBEDDED.configuration]
                    .files
                    .map { file -> if (file.isDirectory) fileTree(file) else zipTree(file) }
                    .forEach { from(it) }

            var jarBaseName = archiveBaseName.get()
            if (project.hasProperty(ARCHIVE_BASE_NAME)) {
                jarBaseName = properties[ARCHIVE_BASE_NAME] as String
            }
            if (project.hasProperty(JAR_BASE_NAME)) {
                jarBaseName = properties[JAR_BASE_NAME] as String
            }

            var jarFullName = (project.version as String?)?.let { version ->
                if (version.isBlank()) {
                    return@let jarBaseName
                }
                return@let "$jarBaseName-${version.toLowerCase()
                        .trim()
                        .replace(SPACE, DASH)
                        .replace(SLASH, DASH)
                        .replace(BACKWARD_SLASH, DASH)}$DOT$JAR_EXTENSION"
            } ?: "jarBaseName$DOT$JAR_EXTENSION"
            if (project.hasProperty(ARCHIVE_FULL_NAME)) {
                jarFullName = properties[ARCHIVE_FULL_NAME] as String
            }
            if (project.hasProperty(JAR_FULL_NAME)) {
                jarFullName = properties[JAR_FULL_NAME] as String
            }

            projectExtension().javaConfiguration
                    .jarName
                    ?.let(archiveFileName::set)
                    ?: archiveFileName.set(jarFullName)

            doFirst {
                if (projectExtension().mainClass.isBlank()) {
                    determineMainClass()?.let(projectExtension()::mainClass)
                }
                manifest { manifest ->
                    manifest.attributes[MAIN_CLASS_ATTRIBUTE] = projectExtension().mainClass

                    if (targetCompatibility.isJava9Compatible) {
                        manifest.attributes[MULTI_RELEASE_ATTRIBUTE] = true
                    }
                }
                exclude(MANIFEST_EXCLUSIONS)
                if (projectExtension().mainClass.isNotEmpty()) {
                    attention("Main class: '${projectExtension().mainClass}'")
                }
            }
        }
    }
}