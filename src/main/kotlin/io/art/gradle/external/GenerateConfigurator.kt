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

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.getByName

fun Project.configureGenerate() {
    tasks.getByName<JavaCompile>("compileJava") {
        with(options) {
            isFork = true
            compilerArgs.addAll(mutableListOf(
                    "-Aart.generator.recompilation.destination=${destinationDir.absolutePath}",
                    "-Aart.generator.recompilation.classpath=${classpath.files.joinToString(",")}",
                    "-Aart.generator.recompilation.sources=${source.files.joinToString(",")}",
                    "-Aart.generator.recompilation.generatedSourcesRoot=${options.annotationProcessorGeneratedSourcesDirectory}"
            ))
            if (!JavaVersion.current().isJava8) {
                compilerArgs.addAll(arrayOf(
                        "--add-exports", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
                        "--add-exports", "jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
                        "--add-exports", "jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED",
                        "--add-exports", "jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
                        "--add-exports", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
                        "--add-exports", "jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
                        "--add-exports", "jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
                        "--add-exports", "jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED"
                ))
            }
        }
    }
}