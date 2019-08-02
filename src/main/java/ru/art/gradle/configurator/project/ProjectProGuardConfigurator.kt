/*
 * Copyright 2019 ART
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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