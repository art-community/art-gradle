/*
 * ART Java
 *
 * Copyright 2019 ART
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

package ru.art.gradle.determinator

import org.gradle.api.*
import ru.art.gradle.constants.*
import java.io.File.*

fun Project.determineSourceSets(): SourceSetDeterminationResult {
    var hasGroovy = false
    var hasScala = false
    var hasKotlin = false
    var hasGatling = false
    var hasJmh = false
    var hasWeb = false
    var hasGroovyTests = false
    var hasScalaTests = false
    var hasKotlinTests = false
    val testSourceSet = file("$projectDir$separator$SRC$separator$TEST_SOURCE_SET")
    if (testSourceSet.exists()) {
        testSourceSet.listFiles()?.forEach { file ->
            hasGroovyTests = hasGroovyTests or file.name.contains(GROOVY)
            hasScalaTests = hasScalaTests or file.name.contains(SCALA)
            hasKotlinTests = hasKotlinTests or file.name.contains(KOTLIN)
        }
    }
    file("$projectDir$separator$SRC").walkTopDown()
            .maxDepth(2)
            .filter { !it.absolutePath.contains("$SRC$separator$TEST_SOURCE_SET") }
            .forEach { file ->
                hasGroovy = hasGroovy or file.name.contains(GROOVY)
                hasGatling = hasGatling or file.name.contains(GATLING)
                hasScala = hasScala or file.name.contains(SCALA)
                hasJmh = hasJmh or file.name.contains(JMH)
                hasKotlin = hasKotlin or file.name.contains(KOTLIN)
                hasWeb = hasWeb or file.name.contains(WEB)
            }
    return SourceSetDeterminationResult(
            hasGroovy = hasGroovy,
            hasScala = hasScala,
            hasKotlin = hasKotlin,
            hasGatling = hasGatling,
            hasJmh = hasJmh,
            hasWeb = hasWeb,
            hasGroovyTests = hasGroovyTests,
            hasScalaTests = hasScalaTests,
            hasKotlinTests = hasKotlinTests)
}

data class SourceSetDeterminationResult(val hasGroovy: Boolean,
                                        val hasScala: Boolean,
                                        val hasKotlin: Boolean,
                                        val hasGatling: Boolean,
                                        val hasJmh: Boolean,
                                        val hasWeb: Boolean,
                                        val hasGroovyTests: Boolean,
                                        val hasScalaTests: Boolean,
                                        val hasKotlinTests: Boolean)