/*
 * ART
 *
 * Copyright 2020 ART
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

package io.art.gradle

import configureExecutableJar
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.get

/*
 * ART
 *
 * Copyright 2020 ART
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

class JavaGeneratorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            val compileJava: JavaCompile = tasks["compileJava"] as JavaCompile

            configureExecutableJar()

            compileJava.doFirst {
                compileJava.options.compilerArgs.addAll(arrayOf(
                        "-Aart.generator.recompilation.destination=${compileJava.destinationDir.absolutePath}",
                        "-Aart.generator.recompilation.classpath=${compileJava.classpath.files.joinToString(",")}",
                        "-Aart.generator.recompilation.sources=${compileJava.source.files.joinToString(",")}"
                ))
            }
            //compileJava.dependsOn("clean").dependsOn(project(":language-java").tasks["build"])
        }
    }
}
