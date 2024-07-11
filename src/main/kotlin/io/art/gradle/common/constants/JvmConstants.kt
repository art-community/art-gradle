/*
 * ART
 *
 * Copyright 2019-2022 ART
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

import org.gradle.internal.os.OperatingSystem.*
import java.io.File
import java.time.Duration
import java.time.Duration.ofMinutes

const val MAIN_CLASS_MANIFEST_ATTRIBUTE = "Main-Class"
const val MULTI_RELEASE_MANIFEST_ATTRIBUTE = "Multi-Release"
const val BUILD_JAR_EXECUTABLE_TASK = "build-jar-executable"
const val RUN_JAR_EXECUTABLE_TASK = "run-jar-executable"
const val PACKAGE_TASK = "package"
const val BUILD_JAR_TEST_TASK = "build-jar-test"
const val RUN_JAR_TEST_TASK = "run-jar-test"
const val JAVA = "java"
const val JRE = "jre"
const val JAR_OPTION = "-jar"
const val RUNTIME = "runtime"
const val BIN = "bin"
const val JLINK = "jlink"
const val JMODS = "jmods"

val PACKAGE_JRE_DOWNLOAD_TIMEOUT: Duration = ofMinutes(5)

val PACKAGE_JRE_URL by lazy {
    when {
        current().isLinux -> "https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_linux-x64_bin.tar.gz"
        current().isMacOsX -> "https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_macos-aarch64_bin.tar.gz"
        else -> "https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_windows-x64_bin.zip"
    }
}

val JLINK_OPTIONS = { runtime: File ->
    arrayOf(
        "--module-path", "libs:${runtime.resolve(JMODS)}",
        "--add-modules", "java.base,jdk.unsupported",
        "--bind-services",
        "--no-header-files",
        "--no-man-pages",
        "--strip-debug",
        "--compress=zip-9",
        "--output",
    )
}

val DEFAULT_JAR_EXCLUSIONS = setOf("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/**.RSA", "META-INF/MANIFEST.MF")
