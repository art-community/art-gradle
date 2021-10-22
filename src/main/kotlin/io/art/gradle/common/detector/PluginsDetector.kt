package io.art.gradle.common.detector

import io.art.gradle.common.constants.KOTLIN_JVM_PLUGIN_ID
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPlatformPlugin


val Project.hasJavaPlugin
    get() = plugins.hasPlugin(JavaBasePlugin::class.java)
            || plugins.hasPlugin(JavaLibraryPlugin::class.java)
            || plugins.hasPlugin(JavaPlatformPlugin::class.java)

val Project.hasKotlinPlugin
    get() = plugins.hasPlugin(KOTLIN_JVM_PLUGIN_ID)
