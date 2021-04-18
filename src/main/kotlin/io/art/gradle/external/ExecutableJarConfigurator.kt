/*
 * ART
 *
 * Copyright 2019-2021 ART
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.art.gradle.external

import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar

fun Project.configureExecutableJar(additional: Jar.() -> Unit = { }) {
    tasks.register("executableJar", Jar::class.java) {
//        val compileJava: JavaCompile = tasks["compileJava"] as JavaCompile
//        val processResources: Task = tasks["processResources"]
//        val runtimeClasspath = configurations["runtimeClasspath"]
//        val compileClasspath = configurations["compileClasspath"]
//
//        group = "build"
//        dependsOn(":build")
//
//        manifest {
//            attributes(mapOf("Main-Class" to "ru.Example"))
//        }
//
//        from(processResources.outputs.files)
//        from(compileJava.outputs.files)
//        from(compileClasspath.filter { it.extension != "gz" }.map { if (it.isDirectory) it else zipTree(it) })
//        from(runtimeClasspath.filter { it.extension != "gz" }.map { if (it.isDirectory) it else zipTree(it) })
//        additional()
    }
}
