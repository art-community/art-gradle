package io.art.gradle.common.local

import io.art.gradle.common.constants.LOCAL_PROPERTIES_FILE
import org.gradle.api.Project
import java.util.*

val Project.localProperties: Properties?
    get() = file(projectDir.resolve(LOCAL_PROPERTIES_FILE))
            .takeIf { file -> file.exists() }
            ?.let { file -> Properties().apply { file.inputStream().use(::load) } }

fun Project.getLocalProperty(name: String) = localProperties?.get(name)

fun Project.hasLocalProperty(name: String) = localProperties?.containsKey(name)

fun Project.booleanProperty(name: String): Boolean {
    val projectProperty = hasProperty(name) && property(name)?.toString()?.toBoolean() == true
    val localProperty = getLocalProperty(name)?.toString()?.toBoolean() == true
    return projectProperty || localProperty
}
