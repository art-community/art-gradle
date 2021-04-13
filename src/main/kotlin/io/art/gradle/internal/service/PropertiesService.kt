package io.art.gradle.internal.service

import io.art.gradle.internal.constants.PUBLISHING_PROPERTIES
import org.gradle.api.Project
import java.util.*

fun Project.loadProperties(name: String): Map<String, String> {
    val content = properties[name] as String?
            ?: rootDir.parentFile?.resolve(PUBLISHING_PROPERTIES)?.readText()
            ?: return emptyMap()
    return Properties()
            .apply { load(content.reader()) }
            .entries
            .associate { entry -> "${entry.key}" to "${entry.value}" }
}
