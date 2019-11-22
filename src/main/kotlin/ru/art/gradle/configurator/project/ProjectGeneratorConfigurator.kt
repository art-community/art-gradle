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
import org.gradle.api.file.*
import org.gradle.api.plugins.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.compile.*
import org.gradle.internal.classloader.*
import org.gradle.kotlin.dsl.*
import ru.art.generator.soap.model.*
import ru.art.generator.soap.service.SoapGeneratorService.*
import ru.art.gradle.*
import ru.art.gradle.configuration.SoapGeneratorConfiguration.GenerationMode.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.DependencyConfiguration.*
import ru.art.gradle.context.Context.projectExtension
import ru.art.gradle.logging.*
import java.io.File.*
import java.nio.file.*
import java.nio.file.Files.*
import ru.art.generator.mapper.Generator as MappersGenerator
import ru.art.generator.soap.service.SoapGeneratorService as SoapGenerator

fun Project.configureGenerator() {
    val mainSourceSet = this@configureGenerator.convention.getPlugin(JavaPluginConvention::class.java).sourceSets.getAt(MAIN_SOURCE_SET)
    val sourceDirectories = mainSourceSet.java.sourceDirectories
    if (sourceDirectories.isEmpty) {
        additionalAttention("Java sources don't exists so disable ART generator")
        return
    }
    val packagePath = projectExtension().generatorConfiguration.packageName.replace(DOT, separator)
    val packageDirectory = "${sourceDirectories.first().absolutePath}$separator$packagePath"
    if (file("$packageDirectory$separator$MODEL_PACKAGE").exists()) {
        createGenerateMappersTask(mainSourceSet).dependsOn(createCompileTask(mainSourceSet, packageDirectory))
        success("Created '$GENERATE_MAPPERS_TASK' task depends on '$COMPILE_MODELS_TASK' task, running mappers generator")
    }
}

fun Project.configureSoapGenerator() {
    val mainSourceSet = this@configureSoapGenerator.convention.getPlugin(JavaPluginConvention::class.java).sourceSets.getAt(MAIN_SOURCE_SET)
    createGenerateSoapEntitiesTask(mainSourceSet)
    success("Created '$GENERATE_SOAP_ENTITIES_TASK' task, running SOAP models & mappers generator")
}

private fun Project.createGenerateMappersTask(mainSourceSet: SourceSet): Task = tasks.create(GENERATE_MAPPERS_TASK) { task ->
    with(task) {
        group = GENERATOR_GROUP
        doLast {
            val visitableURLClassLoader = ProjectPlugin::class.java.classLoader as VisitableURLClassLoader
            visitableURLClassLoader.addURL(mainSourceSet.java.outputDir.toURI().toURL())
            configurations[COMPILE_CLASSPATH.configuration]
                    .files
                    .forEach { file -> visitableURLClassLoader.addURL(file.toURI().toURL()) }
            val packagePath = projectExtension().generatorConfiguration.packageName.replace(DOT, separator)
            val sourcesPath = mainSourceSet.java.outputDir.absolutePath
            visitableURLClassLoader.loadClass(MappersGenerator::class.java.name)
            MappersGenerator.performGeneration("$sourcesPath$separator$packagePath", MODEL_PACKAGE, MAPPING_PACKAGE)
        }
    }
}

private fun Project.createGenerateSoapEntitiesTask(mainSourceSet: SourceSet): Task = tasks.create(GENERATE_SOAP_ENTITIES_TASK) { task ->
    with(task) {
        group = GENERATOR_GROUP
        doLast {
            val packagePath = projectExtension().generatorConfiguration.soapConfiguration.packageName.replace(DOT, separator)
            val packageDirectory = file("$SRC_MAIN_JAVA$separator$packagePath$separator$MODEL_PACKAGE")
            if (!packageDirectory.exists()) {
                createDirectories(Paths.get(packageDirectory.absolutePath))
            }

            val visitableURLClassLoader = ProjectPlugin::class.java.classLoader as VisitableURLClassLoader
            visitableURLClassLoader.addURL(mainSourceSet.java.outputDir.toURI().toURL())
            configurations[COMPILE_CLASSPATH.configuration]
                    .files
                    .forEach { file -> visitableURLClassLoader.addURL(file.toURI().toURL()) }
            visitableURLClassLoader.loadClass(SoapGenerator::class.java.name)
            projectExtension().generatorConfiguration
                    .soapConfiguration
                    .generationRequests
                    .forEach { request ->
                        SoapGenerationRequest
                                .builder()
                                .wsdlUrl(request.wsdlUrl)
                                .absolutePathToSrcMainJava(file(SRC_MAIN_JAVA).absolutePath)
                                .generationMode(when (request.generationMode) {
                                    CLIENT -> SoapGenerationMode.CLIENT
                                    SERVER -> SoapGenerationMode.SERVER
                                })
                                .packageName(request.packageName)
                                .build()
                                .apply(::performGeneration)
                    }
        }
    }
}

private fun Project.createCompileTask(mainSourceSet: SourceSet, packageDir: String): JavaCompile =
        tasks.create(COMPILE_MODELS_TASK, JavaCompile::class.java) { task ->
            with(task) {
                group = GENERATOR_GROUP
                options.isIncremental = false
                options.isFork = true
                options.annotationProcessorPath = configurations[ANNOTATION_PROCESSOR.configuration]
                options.isFailOnError = false
                source = projectExtension().generatorConfiguration
                        .compileModelsSourcePackages
                        .map { source -> fileTree(packageDir + separator + source) }
                        .reduce(FileTree::plus)
                classpath = configurations[COMPILE_CLASSPATH.configuration] +
                        configurations[RUNTIME_CLASSPATH.configuration] +
                        configurations[ANNOTATION_PROCESSOR.configuration]
                destinationDir = mainSourceSet.java.outputDir
            }
        }
