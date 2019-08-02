package ru.art.gradle.configurator.project

import org.gradle.api.*
import org.gradle.api.plugins.*
import ru.art.gradle.constants.*
import ru.art.gradle.exception.*
import java.io.File.*
import java.lang.reflect.Modifier.*
import java.net.*

fun Project.determineMainClass() = with(convention.getPlugin(JavaPluginConvention::class.java)) {
    val mainSourceSet = sourceSets.getByName(MAIN_SOURCE_SET)
    val classes = mainSourceSet.output
            .classesDirs
            .asFileTree
            .filter { file -> file.absolutePath.endsWith(CLASS_FILE_EXTENSION) }
            .map { file -> file.absolutePath.substringAfter(MAIN_DIR).replace(separator, DOT).removeSuffix(CLASS_FILE_EXTENSION) }
    val classesPath = mainSourceSet.output.classesDirs
            .firstOrNull()
            ?.absolutePath
            ?: return@with null
    val urls = (listOf(classesPath) + mainSourceSet.compileClasspath.files.map { it.absolutePath })
            .map { uri(it).toURL() }
            .toTypedArray()
    val classLoader = URLClassLoader(urls, javaClass.classLoader)
    classes.map { ignoreException({ classLoader.loadClass(it) }, { null }) }
            .firstOrNull { clazz ->
                clazz?.methods
                        ?.toList()
                        ?.any { method ->
                            method.name == MAIN_METHOD_NAME
                                    && isStatic(method.modifiers)
                                    && isPublic(method.modifiers)
                                    && method.parameters.size == 1
                                    && method.parameters[0].type == Array<String>::class.java
                        }
                        ?: false
            }?.name
}