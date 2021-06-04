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

package io.art.gradle.common.constants

const val MAIN_CLASS_MANIFEST_ATTRIBUTE = "Main-Class"
const val MULTI_RELEASE_MANIFEST_ATTRIBUTE = "Multi-Release"
const val JAR = "jar"
const val BUILD_EXECUTABLE_JAR_TASK = "build-executable-jar"
const val RUN_EXECUTABLE_JAR_TASK = "run-executable-jar"
const val JAVA = "java"
const val JAR_OPTION = "-jar"

val DEFAULT_JAR_EXCLUSIONS = setOf("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/**.RSA", "META-INF/MANIFEST.MF")
