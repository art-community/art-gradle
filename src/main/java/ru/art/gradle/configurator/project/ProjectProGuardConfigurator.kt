package ru.art.gradle.configurator.project

import org.gradle.api.*
import org.gradle.api.plugins.*
import proguard.gradle.*
import ru.art.gradle.constants.*
import ru.art.gradle.provider.*

fun Project.addProGuardTask() {
    if (convention.getPlugin(JavaPluginConvention::class.java).sourceSets.getAt(MAIN_SOURCE_SET).java.files.isEmpty()) {
        return
    }
    val proGuardTask = tasks.create(PRO_GUARD_TASK, ProGuardTask::class.java)
    with(proGuardTask) {
        doFirst {
            injars(jarTask().archiveFile.get())
            outjars(jarTask().archiveFile.get().asFile.absolutePath.replace(JAR_EXTENSION, OPTIMIZED_JAR_POSTFIX))
        }
        doLast {
            file(jarTask().archiveFile.get().asFile.absolutePath.replace(JAR_EXTENSION, OPTIMIZED_JAR_POSTFIX)).renameTo(jarTask().archiveFile.get().asFile)
        }
        dontwarn()
        dontnote()
        dontobfuscate()
        ignorewarnings()
        keepdirectories()
        keep(RU_ART_CLASSES_SPECIFICATION)
        KEEP_ATTRIBUTES.forEach(::keepattributes)
        jarTask().finalizedBy(this)
    }
}