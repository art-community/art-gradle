package ru.art.configurator.project

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import proguard.gradle.ProGuardTask
import ru.art.constants.*
import ru.art.provider.jarTask

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