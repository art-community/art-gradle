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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

class JavaGeneratorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            val classpath = configurations
                    .filter { configuration -> configuration.isCanBeResolved }
                    .flatMap { configuration -> configuration.files }
            val compileJava = tasks.getAt("compileJava") as JavaCompile
            compileJava.options.compilerArgs.addAll(arrayOf(
                    "-Aart.generator.recompilation.destination=${compileJava.destinationDir.absolutePath}",
                    "-Aart.generator.recompilation.classpath=${classpath.joinToString(",")}",
                    "-Aart.generator.recompilation.sources=${compileJava.source.files.joinToString(",")}",
                    "-Aart.generator.recompilation.generatedSourcesRoot=${compileJava.options.annotationProcessorGeneratedSourcesDirectory}"
            ))

            configureExecutableJar()

        }
    }
}
