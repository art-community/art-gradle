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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class KotlinGeneratorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            val compileKotlin: KotlinCompile = tasks["compileKotlin"] as KotlinCompile
            configureExecutableJar { from(compileKotlin.outputs.files) }
            with(the<KaptExtension>()) {
                val compileClasspath = configurations["compileClasspath"]
                includeCompileClasspath = false
                useBuildCache = false
                javacOptions {
                    arguments {
                        arg("art.generator.recompilation.destination", compileKotlin
                                .destinationDir
                                .absolutePath)
                        arg("art.generator.recompilation.classpath", compileClasspath
                                .files
                                .toSet()
                                .joinToString(","))
                        arg("art.generator.recompilation.sources", compileKotlin
                                .source
                                .files
                                .joinToString(","))
                    }
                }
            }
        }
    }
}
