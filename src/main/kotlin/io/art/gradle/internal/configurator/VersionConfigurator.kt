package io.art.gradle.internal.configurator

import io.art.gradle.internal.constants.EMPTY_STRING
import io.art.gradle.internal.service.branch
import org.gradle.api.Project
import org.gradle.api.Project.DEFAULT_VERSION

fun Project.resolveVersion(): String = when {
    version != EMPTY_STRING
            && version != DEFAULT_VERSION
            && version.toString().trim() != EMPTY_STRING -> version.toString()
    else -> branch
}
