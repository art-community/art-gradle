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

import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.process.CommandLineArgumentProvider

fun Project.configureGenerate() {
    val compileJava = tasks.findByPath("compileJava") as? JavaCompile ?: return

    class CompileJavaGeneratorOptionsProvider : CommandLineArgumentProvider, Named {
        @Internal
        override fun getName(): String = CompileJavaGeneratorOptionsProvider::class.simpleName!!

        override fun asArguments(): Iterable<String> = mutableListOf(
                "-Aart.generator.recompilation.destination=${compileJava.destinationDir.absolutePath}",
                "-Aart.generator.recompilation.classpath=${compileJava.classpath.files.joinToString(",")}",
                "-Aart.generator.recompilation.sources=${compileJava.source.files.joinToString(",")}",
                "-Aart.generator.recompilation.generatedSourcesRoot=${compileJava.options.annotationProcessorGeneratedSourcesDirectory}"
        )
    }
    compileJava.options.compilerArgumentProviders += CompileJavaGeneratorOptionsProvider()
}
