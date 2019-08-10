/*
 * Copyright 2019 ART
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.art.gradle.configurator.project

import org.gradle.api.*
import org.gradle.api.plugins.*
import org.gradle.api.tasks.*
import org.gradle.internal.classloader.*
import ru.art.gradle.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.DependencyConfiguration.*
import ru.art.gradle.constants.SpecificationType.*
import ru.art.gradle.context.Context.projectExtension
import ru.art.gradle.logging.*
import ru.art.gradle.provider.*
import java.io.File.*
import ru.art.generator.mapper.Generator as MappersGenerator
import ru.art.generator.spec.http.proxyspec.Generator as HttpCommunicationSpecificationsGenerator
import ru.art.generator.spec.http.servicespec.Generator as HttpSpecificationsGenerator

fun Project.configureGenerator() {
    val mainSourceSet = this@configureGenerator.convention.getPlugin(JavaPluginConvention::class.java).sourceSets.getAt(MAIN_SOURCE_SET)
    val sourceDirectories = mainSourceSet.java.sourceDirectories
    if (sourceDirectories.isEmpty) {
        additionalAttention("Java sources don't exists so disable ART generator")
        return
    }
    val packagePath = projectExtension().generatorConfiguration.packageName.replace(DOT, separator)
    val packageDir = "${sourceDirectories.first().absolutePath}$separator$packagePath"
    if (file("$packageDir$separator$MODEL_PACKAGE").exists()) {
        createGenerateMappersTask(mainSourceSet).dependsOn(compileJavaTask())
        success("Created 'generateMappers' task depends on 'buildModel' task, running mappers generator and finalized by 'build' task")
    }
    if (file("$packageDir$separator$SERVICE_PACKAGE").exists()) {
        file("$packageDir$separator$SERVICE_PACKAGE")
                .walkTopDown()
                .filter { file -> file.isFile && file(file.parent).name == SERVICE_PACKAGE }
                .map { file -> file.name.removeSuffix(JAVA_FILE_EXTENSION) }
                .flatMap { serviceName ->
                    SpecificationType.values()
                            .map { type -> createGenerateSpecificationTask(type, packageDir, serviceName) }
                            .asSequence()
                }
                .onEach { task -> task.dependsOn(compileJavaTask()) }
    }
}

private fun Project.createGenerateMappersTask(mainSourceSet: SourceSet): Task = tasks.create(GENERATE_MAPPERS_TASK) { task ->
    with(task) {
        group = GENERATOR_GROUP
        doLast {
            val visitableURLClassLoader = ProjectPlugin::class.java.classLoader as VisitableURLClassLoader
            visitableURLClassLoader.addURL(mainSourceSet.java.outputDir.toURI().toURL())
            configurations
                    .getByName(COMPILE_CLASSPATH.configuration)
                    .files
                    .forEach { file -> visitableURLClassLoader.addURL(file.toURI().toURL()) }
            val packagePath = projectExtension().generatorConfiguration.packageName.replace(DOT, separator)
            val sourcesPath = mainSourceSet.java.outputDir.absolutePath
            visitableURLClassLoader.loadClass(MappersGenerator::class.java.name)
            MappersGenerator.performGeneration("$sourcesPath$separator$packagePath", MODEL_PACKAGE, MAPPING_PACKAGE)
        }
    }
}

private fun Project.createGenerateSpecificationTask(type: SpecificationType, packageDir: String, service: String): Task {
    val mainSourceSet = convention.getPlugin(JavaPluginConvention::class.java).sourceSets.getAt(MAIN_SOURCE_SET)
    val name = when (type) {
        HTTP -> GENERATE_HTTP_SPECIFICATION_TASK(service)
        HTTP_COMMUNICATION -> GENERATE_HTTP_PROXY_SPECIFICATION_TASK(service)
        GRPC -> GENERATE_GRPC_SPECIFICATION_TASK(service)
        RSOCKET -> GENERATE_RSOCKET_SPECIFICATION_TASK(service)
        SOAP -> GENERATE_SOAP_SPECIFICATION_TASK(service)
    }
    val group = when (type) {
        HTTP -> GENERATOR_HTTP_GROUP
        HTTP_COMMUNICATION -> GENERATOR_HTTP_GROUP
        GRPC -> GENERATOR_GRPC_GROUP
        RSOCKET -> GENERATOR_RSOCKET_GROUP
        SOAP -> GENERATOR_SOAP_GROUP
    }
    val visitableURLClassLoader = ProjectPlugin::class.java.classLoader as VisitableURLClassLoader
    visitableURLClassLoader.addURL(mainSourceSet.java.outputDir.toURI().toURL())
    success("Created '$name' task depends on 'buildService' task, running service specification generator for service $service and finalized by 'build' task")
    return tasks.create(name) { task ->
        with(task) {
            this.group = group
            doLast {
                configurations
                        .getByName(COMPILE_CLASSPATH.configuration)
                        .files
                        .forEach { file -> visitableURLClassLoader.addURL(file.toURI().toURL()) }
                if (mainSourceSet.java.outputDir.listFiles().isNullOrEmpty()) {
                    return@doLast
                }
                val serviceClass = mainSourceSet.java.outputDir
                        .walkTopDown()
                        .first { it.nameWithoutExtension == service }
                        .absolutePath
                        .substringAfter("$MAIN_SOURCE_SET$separator")
                        .removeSuffix(CLASS_FILE_EXTENSION)
                        .replace(separator, DOT)
                when (type) {
                    HTTP -> {
                        visitableURLClassLoader.loadClass(HttpSpecificationsGenerator::class.java.name)
                        HttpSpecificationsGenerator.performGeneration(packageDir, visitableURLClassLoader.loadClass(serviceClass))
                    }
                    HTTP_COMMUNICATION -> {
                        visitableURLClassLoader.loadClass(HttpCommunicationSpecificationsGenerator::class.java.name)
                        HttpCommunicationSpecificationsGenerator.performGeneration(packageDir, visitableURLClassLoader.loadClass(serviceClass))
                    }
                    else -> {
                    }
                }
            }
        }
    }
}