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

package ru.art.gradle.constants

enum class DependencyConfiguration(val configuration: String) {
    DEFAULT("default"),
    ANNOTATION_PROCESSOR("annotationProcessor"),
    COMPILE_CLASSPATH("compileClasspath"),
    COMPILE_ONLY("compileOnly"),
    RUNTIME_CLASSPATH("runtimeClasspath"),
    COMPILE("compile"),
    IMPLEMENTATION("implementation"),
    TEST_IMPLEMENTATION("testImplementation"),
    EMBEDDED("embedded"),
    PROVIDED("provided"),
    EXTERNAL("external"),
    TEST_COMPILE_CLASSPATH("testCompileClasspath"),
    TEST_RUNTIME_CLASSPATH("testRuntimeClasspath"),
    TEST_COMPILE_ONLY("testCompileOnly"),
    CLASSPATH("classpath"),
    GATLING("gatling"),
    GATLING_COMPILE("gatlingCompile"),
    GATLING_RUNTIME("gatlingRuntime"),
    API("api")
}
