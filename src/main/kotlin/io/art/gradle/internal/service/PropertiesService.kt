package io.art.gradle.internal.service

import org.gradle.api.Project
import java.io.File
import java.util.*

fun Project.loadProperties(name: String): Map<String, String> {
    val content = rootDir.parentFile
            ?.resolve(name)
            ?.takeIf(File::exists)
            ?.readText()
            ?: return emptyMap()
    return Properties()
            .apply { load(content.reader()) }
            .entries
            .associate { entry -> "${entry.key}" to "${entry.value}" }
}

fun Project.loadProperty(name: String, source: String): String? =
        loadProperties(source)[name] ?: properties[name]?.toString()
