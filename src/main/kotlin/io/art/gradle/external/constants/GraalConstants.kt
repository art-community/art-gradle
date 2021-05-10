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

import io.art.gradle.external.constants.GraalJavaVersion.JAVA_11
import io.art.gradle.external.constants.GraalJavaVersion.JAVA_8
import io.art.gradle.external.constants.GraalPlatformName.*
import io.art.gradle.external.model.ProcessorArchitecture
import org.gradle.internal.os.OperatingSystem
import java.net.URL
import java.nio.file.Path

const val GRAAL = "graal"

val GRAAL_NATIVE_IMAGE_EXECUTABLE = when {
    OperatingSystem.current().isWindows -> "native-image.cmd"
    else -> "native-image"
}

val GRAAL_UPDATER_EXECUTABLE = when {
    OperatingSystem.current().isWindows -> "gu.cmd"
    else -> "gu"
}

val GRAAL_BASE_RESOURCE_CONFIGURATION_PATH = { javaVersion: GraalJavaVersion, resource: String ->
    when (javaVersion) {
        JAVA_8 -> "external/graal/jdk-8/$resource"
        JAVA_11 -> "external/graal/jdk-11/$resource"
    }
}

var GRAAL_UPDATE_NATIVE_IMAGE_ARGUMENTS = listOf("install", "native-image")
var GRAAL_UPDATE_LLVM_ARGUMENTS = listOf("install", "llvm-toolchain")

const val GRAALVM_RELEASES_BASE_URL = "https://github.com/graalvm/graalvm-ce-builds/releases/download"

val GRAAL_ARCHIVE_NAME = { platform: GraalPlatformName, java: GraalJavaVersion, architecture: GraalArchitectureName, version: String ->
    when (platform) {
        WINDOWS -> "graalvm-ce-${java.version}-${platform.platform}-${architecture.architecture}-$version.zip"
        LINUX, DARWIN -> "graalvm-ce-${java.version}-${platform.platform}-${architecture.architecture}-$version.tar.gz"
    }
}
val GRAAL_UNPACKED_NAME = { java: GraalJavaVersion, version: String ->
    "graalvm-ce-${java.version}-$version"
}
val GRAAL_DOWNLOAD_URL = { archive: String, version: String ->
    URL("$GRAALVM_RELEASES_BASE_URL/vm-${version}/${archive}")
}

const val GRAAL_JNI_CONFIGURATION = "jni-config.json"
const val GRAAL_PROXY_CONFIGURATION = "proxy-config.json"
const val GRAAL_REFLECTION_CONFIGURATION = "reflect-config.json"
const val GRAAL_RESOURCE_CONFIGURATION = "resource-config.json"

val GRAAL_CONFIGURATION_FILES = listOf(
        GRAAL_JNI_CONFIGURATION,
        GRAAL_PROXY_CONFIGURATION,
        GRAAL_REFLECTION_CONFIGURATION,
        GRAAL_RESOURCE_CONFIGURATION
)

var GRAAL_MANDATORY_OPTIONS = listOf(
        "-H:+ReportExceptionStackTraces",
        "--enable-http",
        "--enable-https",
        "--enable-url-protocols",
        "--enable-url-protocols",
        "--enable-all-security-services",
        "--install-exit-handlers",
        "--no-server",
        "--no-fallback",
        "--report-unsupported-elements-at-runtime",
        "--allow-incomplete-classpath",
        "--initialize-at-build-time=org.apache.logging.log4j",
        "--initialize-at-run-time=reactor.netty,io.netty,io.rsocket,io.tarantool,org.apache.logging.log4j.core.pattern.JAnsiTextRenderer"
)

val GRAAL_CONFIGURATIONS_PATH_OPTION = { path: Path -> "-H:ConfigurationFileDirectories=${path.toAbsolutePath()}" }

const val GRAAL_WINDOWS_VISUAL_STUDIO_VARS_SCRIPT_PROPERTY = "windowsVisualStudioVarsScript"

val GRAAL_WINDOWS_LAUNCH_SCRIPT = { workingDirectory: Path, visualStudioVarsPath: Path, graalOptions: List<String> ->
    """
            cmd.exe /c "call `"${visualStudioVarsPath.toFile().absolutePath}`" && set > ${workingDirectory.resolve("vcvars.environment").toAbsolutePath()}"
            Get-Content "${workingDirectory.resolve("vcvars.environment").toAbsolutePath()}" | Foreach-Object { 
                if (${DOLLAR}_-match "^(.*?)=(.*)${DOLLAR}") { 
                    Set-Content "env:\${DOLLAR}(${DOLLAR}matches[1])"${DOLLAR}matches[2] 
                } 
            }
            . ${graalOptions.joinToString(" ")} 
    """.trimIndent()
}

const val GRAAL_WINDOWS_LAUNCH_SCRIPT_NAME = "native-build-windows.ps1"

enum class ProcessorArchitectures(val architecture: ProcessorArchitecture) {
    X86(ProcessorArchitecture("x86", listOf("i386", "ia-32", "i686"))),
    X86_64(ProcessorArchitecture("x86-64", listOf("x86_64", "amd64", "x64"))),
    IA_64(ProcessorArchitecture("ia-64", listOf("ia64"))),
    ARM_V7(ProcessorArchitecture("arm-v7", listOf("armv7", "arm", "arm32"))),
    ARM_V8(ProcessorArchitecture("arm-v8", listOf("aarch64")))
}

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

enum class GraalAgentOutputMode {
    OVERWRITE,
    MERGE
}

const val GRAAL_LLVM_OPTION = "-H:CompilerBackend=llvm"
const val GRAAL_MUSL_OPTION = "--libc=musl"


val GRAAL_AGENT_OUTPUT_DIR_OPTION = { path: Path -> "config-output-dir=${path.toAbsolutePath()}" }
val GRAAL_AGENT_MERGE_DIR_OPTION = { path: Path -> "config-merge-dir=${path.toAbsolutePath()}" }
val GRAAL_AGENT_WRITE_PERIOD_OPTION = { seconds: Long -> "config-write-period-secs=$seconds" }
val GRAAL_AGENT_WRITE_INITIAL_DELAY_OPTION = { seconds: Long -> "config-write-initial-delay-secs=$seconds" }
const val GRAAL_NATIVE_IMAGE_AGENT_OPTION = "-agentlib:native-image-agent"
