package ru.art.gradle.configurator.project

import org.gradle.api.*
import org.gradle.api.file.DuplicatesStrategy.*
import org.gradle.api.plugins.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.DependencyConfiguration.*
import ru.art.gradle.context.Context.projectConfiguration
import ru.art.gradle.logging.*
import ru.art.gradle.provider.*

fun Project.configureJava() {
    val compileJava = compileJavaTask()
    val compileTestJava = compileTestJavaTask()

    if (gradle.gradleVersion.startsWith(GRADLE_VERSION_5)) {
        compileJava.options.annotationProcessorPath = files(configurations.getByName(ANNOTATION_PROCESSOR.configuration).files)
    }
    compileJava.options.encoding = projectConfiguration().javaConfiguration.compilerEncoding

    if (gradle.gradleVersion.startsWith(GRADLE_VERSION_5)) {
        compileTestJava.options.annotationProcessorPath = files(configurations.getByName(ANNOTATION_PROCESSOR.configuration).files)
    }
    compileTestJava.options.encoding = projectConfiguration().javaConfiguration.compilerEncoding

    with(convention.getPlugin(JavaPluginConvention::class.java)) {
        val mainSourceSet = sourceSets.getByName(MAIN_SOURCE_SET)
        val testSourceSet = sourceSets.getByName(TEST_SOURCE_SET)

        mainSourceSet.resources.setSrcDirs(projectConfiguration().resourcesConfiguration.resourceDirs)
        testSourceSet.resources.setSrcDirs(projectConfiguration().resourcesConfiguration.testResourceDirs)

        setSourceCompatibility(projectConfiguration().javaConfiguration.sourceCompatibility)
        setTargetCompatibility(projectConfiguration().javaConfiguration.targetCompatibility)

        with(compileJavaTask()) {
            doLast {
                projectConfiguration().resourcesConfiguration.resourceDirs.forEach { dir ->
                    copy { copy ->
                        with(copy) {
                            from(dir)
                            into(mainSourceSet.java.outputDir)
                        }
                    }
                }
                projectConfiguration().resourcesConfiguration.testResourceDirs.forEach { dir ->
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
            configurations.getByName(EMBEDDED.configuration)
                    .files
                    .map { file -> if (file.isDirectory) fileTree(file) else zipTree(file) }
                    .forEach { from(it) }
            doFirst {
                if (projectConfiguration().mainClass.isBlank()) {
                    determineMainClass()?.let(projectConfiguration()::mainClass)
                }
                manifest { manifest -> manifest.attributes[ru.art.gradle.constants.MAIN_CLASS_ATTRIBUTE] = projectConfiguration().mainClass }
                exclude(ru.art.gradle.constants.MANIFEST_EXCLUSIONS)
                archiveFileName.set("$archiveBaseName-${project.version}${ru.art.gradle.constants.JAR_EXTENSION}")
                if (projectConfiguration().mainClass.isNotEmpty()) {
                    attention("Main class: '${projectConfiguration().mainClass}'")
                }
            }
        }
    }
}