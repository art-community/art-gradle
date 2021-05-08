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

package io.art.gradle.external.constants

import io.art.gradle.external.model.ProcessorArchitecture
import java.net.URL
import java.nio.file.Path


const val GRAAL = "graal"

enum class GraalPlatformName(val platform: String) {
    LINUX("linux"),
    WINDOWS("windows"),
    DARWIN("darwin")
}

enum class GraalArchitectureName(val architecture: String) {
    AMD("amd64"),
    ARM("aarch64")
}

enum class GraalJavaVersion(val version: String) {
    JAVA_8("java8"),
    JAVA_11("java11")
}

enum class GraalVersion(val version: String) {
    LATEST("21.1.0"),
}

val GRAAL_VM_ARCHIVE_NAME = { platform: GraalPlatformName, java: GraalJavaVersion, architecture: GraalArchitectureName, version: String ->
    "graalvm-ce-${java.version}-${platform.platform}-${architecture.architecture}-$version.zip"
}
val GRAAL_VM_UNPACKED_NAME = { java: GraalJavaVersion, version: String ->
    "graalvm-ce-${java.version}-$version"
}
val GRAAL_VM_DOWNLOAD_URL = { archive: String, version: String ->
    URL("https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-${version}/${archive}")
}

var OS_ARCH_PROPERTY = "os.arch"

enum class ProcessorArchitectures(val architecture: ProcessorArchitecture) {
    X86(ProcessorArchitecture("x86", listOf("i386", "ia-32", "i686"))),
    X86_64(ProcessorArchitecture("x86-64", listOf("x86_64", "amd64", "x64"))),
    IA_64(ProcessorArchitecture("ia-64", listOf("ia64"))),
    ARM_V7(ProcessorArchitecture("arm-v7", listOf("armv7", "arm", "arm32"))),
    AARCH64(ProcessorArchitecture("aarch64", emptyList()))
}


var GRAAL_MANDATORY_OPTIONS = listOf(
        "-H:+ReportExceptionStackTraces",
        "--no-fallback",
        "--report-unsupported-elements-at-runtime",
        "--allow-incomplete-classpath",
        "--enable-all-security-services",
        "--initialize-at-build-time=org.apache.logging.log4j",
        "--initialize-at-run-time=reactor.netty,io.netty,io.rsocket,org.apache.logging.log4j.core.pattern.JAnsiTextRenderer"
)


val GRAAL_WINDOWS_LAUNCH_SCRIPT = { visualStudioVarsPath: Path, graalOptions: List<String> ->
    """
            cmd.exe /c "call `"${visualStudioVarsPath.toFile().absolutePath}`" && set > %temp%\vcvars.txt"
            Get-Content "${DOLLAR}env:temp\vcvars.txt" | Foreach-Object { if (${DOLLAR}_-match "^(.*?)=(.*)${DOLLAR}") { Set-Content "env:\${DOLLAR}(${DOLLAR}matches[1])"${DOLLAR}matches[2] } }; . ${graalOptions.joinToString(" ")} 
    """.trimIndent()
}
