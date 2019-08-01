package ru.adk.gradle.plugin.configurator.project

import org.gradle.api.*
import org.gradle.api.plugins.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.compile.*
import org.gradle.internal.classloader.*
import ru.adk.gradle.plugin.constants.*
import ru.adk.gradle.plugin.constants.DependencyConfiguration.*
import ru.adk.gradle.plugin.constants.GeneratorType.*
import ru.adk.gradle.plugin.constants.SpecificationType.*
import ru.adk.gradle.plugin.context.Context.projectConfiguration
import ru.adk.gradle.plugin.logging.*
import ru.adk.gradle.plugin.provider.*
import java.io.File.*
import ru.rti.application.generator.mapper.Generator as MappingGenerator
import ru.rti.application.generator.spec.http.proxyspec.Generator as HttpProxySpecificationsGenerator
import ru.rti.application.generator.spec.http.servicespec.Generator as HttpSpecificationsGenerator

fun Project.configureGenerator() {
    val mainSourceSet = this@configureGenerator.convention.getPlugin(JavaPluginConvention::class.java).sourceSets.getAt(MAIN_SOURCE_SET)
    val packageDir = "${mainSourceSet.java.sourceDirectories.first().absolutePath}$separator${projectConfiguration().generatorConfiguration.packageName.replace(DOT, separator)}"
    if (file("$packageDir$separator$MODEL_PACKAGE").exists()) {
        createGenerateMappersTask(mainSourceSet).dependsOn(createBuildTask(MAPPING, mainSourceSet, "$packageDir$separator$MODEL_PACKAGE")).finalizedBy(buildTask())
        success("Created 'generateMappers' task depends on 'buildModel' task, running mappers generator and finalized by 'build' task")
    }
    if (file("$packageDir$separator$SERVICE_PACKAGE").exists()) {
        val buildService = createBuildTask(SPECIFICATION, mainSourceSet, packageDir)
        file("$packageDir$separator$SERVICE_PACKAGE")
                .walkTopDown()
                .filter { file -> file.isFile && file(file.parent).name == SERVICE_PACKAGE }
                .map { file -> file.name.removeSuffix(JAVA_FILE_EXTENSION) }
                .flatMap { serviceName -> SpecificationType.values().map { type -> createGenerateSpecificationTask(type, packageDir, serviceName) }.asSequence() }
                .onEach { task -> task.dependsOn(buildService) }
                .forEach { task -> task.finalizedBy(buildTask()) }
    }
}

private fun Project.createGenerateMappersTask(mainSourceSet: SourceSet): Task = tasks.create(GENERATE_MAPPERS_TASK) { task ->
    with(task) {
        group = GENERATOR_GROUP
        doLast {
            val visitableURLClassLoader = MappingGenerator::class.java.classLoader as VisitableURLClassLoader
            visitableURLClassLoader.addURL(mainSourceSet.java.outputDir.toURI().toURL())
            configurations
                    .getByName(COMPILE_CLASSPATH.configuration)
                    .files
                    .forEach { file -> visitableURLClassLoader.addURL(file.toURI().toURL()) }
            MappingGenerator.performGeneration("${mainSourceSet.java.outputDir.absolutePath}$separator${projectConfiguration().generatorConfiguration.packageName.replace(DOT, separator)}", MODEL_PACKAGE, MAPPING_PACKAGE)
        }
    }
}

private fun Project.createGenerateSpecificationTask(type: SpecificationType, packageDir: String, service: String): Task {
    val mainSourceSet = convention.getPlugin(JavaPluginConvention::class.java).sourceSets.getAt(MAIN_SOURCE_SET)
    val name = when (type) {
        HTTP -> GENERATE_HTTP_SPECIFICATION_TASK(service)
        HTTP_PROXY -> GENERATE_HTTP_PROXY_SPECIFICATION_TASK(service)
        GRPC -> GENERATE_GRPC_SPECIFICATION_TASK(service)
        RSOCKET -> GENERATE_RSOCKET_SPECIFICATION_TASK(service)
        SOAP -> GENERATE_SOAP_SPECIFICATION_TASK(service)
    }
    val group = when (type) {
        HTTP -> GENERATOR_HTTP_GROUP
        HTTP_PROXY -> GENERATOR_HTTP_GROUP
        GRPC -> GENERATOR_GRPC_GROUP
        RSOCKET -> GENERATOR_RSOCKET_GROUP
        SOAP -> GENERATOR_SOAP_GROUP
    }
    val visitableURLClassLoader = when (type) {
        HTTP -> HttpSpecificationsGenerator::class.java.classLoader as VisitableURLClassLoader
        HTTP_PROXY -> HttpProxySpecificationsGenerator::class.java.classLoader as VisitableURLClassLoader
        else -> return tasks.create(name)
    }

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
                    HTTP -> HttpSpecificationsGenerator.performGeneration(packageDir, visitableURLClassLoader.loadClass(serviceClass))
                    HTTP_PROXY -> HttpProxySpecificationsGenerator.performGeneration(packageDir, visitableURLClassLoader.loadClass(serviceClass))
                    else -> {
                    }
                }
            }
        }
    }
}

private fun Project.createBuildTask(type: GeneratorType, mainSourceSet: SourceSet, packageDir: String): JavaCompile? {
    val name = when (type) {
        MAPPING -> BUILD_MODEL_TASK
        SPECIFICATION -> BUILD_SERVICE_TASK
    }

    return tasks.create(name, JavaCompile::class.java) { task ->
        with(task) {
            group = GENERATOR_GROUP
            options.isIncremental = false
            options.annotationProcessorPath = configurations.getByName(ANNOTATION_PROCESSOR.configuration)
            options.isFailOnError = false
            source = fileTree(packageDir)
            classpath = configurations.getByName(COMPILE_CLASSPATH.configuration) + configurations.getByName(RUNTIME_CLASSPATH.configuration) + configurations.getByName(ANNOTATION_PROCESSOR.configuration)
            destinationDir = mainSourceSet.java.outputDir
        }
    }
}