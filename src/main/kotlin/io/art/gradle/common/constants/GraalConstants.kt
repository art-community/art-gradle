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

import io.art.gradle.common.constants.GraalPlatformName.*
import io.art.gradle.common.model.ProcessorArchitecture
import org.gradle.internal.os.OperatingSystem
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration
import java.time.Duration.ofMinutes

const val GRAAL = "graal"

const val GRAAL_UNIX_UPDATER = "gu"
const val GRAAL_UNIX_NATIVE_IMAGE = "native-image"

val GRAAL_NATIVE_IMAGE_EXECUTABLE = when {
    OperatingSystem.current().isWindows -> "native-image.cmd"
    else -> GRAAL_UNIX_NATIVE_IMAGE
}

val GRAAL_UPDATER_EXECUTABLE = when {
    OperatingSystem.current().isWindows -> "gu.cmd"
    else -> GRAAL_UNIX_UPDATER
}

var GRAAL_UPDATE_NATIVE_IMAGE_ARGUMENTS = listOf("install", "native-image")
var GRAAL_UPDATE_LLVM_ARGUMENTS = listOf("install", "llvm-toolchain")

const val GRAALVM_RELEASES_BASE_URL = "https://github.com/graalvm/graalvm-ce-builds/releases/download"
const val GRAALVM_DEFAULT_VERSION = "jdk-21.0.2"
const val GRAALVM_DEFAULT_PACKAGE_PREFIX = "graalvm-community-jdk-21.0.2"

val GRAAL_ARCHIVE_NAME = { platform: GraalPlatformName, prefix: String, architecture: GraalArchitectureName ->
    when (platform) {
        WINDOWS -> "${prefix}_${platform.platform}-${architecture.architecture}_bin.zip"
        LINUX, DARWIN -> "${prefix}_${platform.platform}-${architecture.architecture}_bin.tar.gz"
    }
}

val GRAAL_DOWNLOAD_URL = { version: String, archive: String ->
    URI.create("$GRAALVM_RELEASES_BASE_URL/$version/$archive").toURL()
}

var GRAAL_DEFAULT_OPTIONS = listOf(
    "-H:+ReportExceptionStackTraces",
    "-H:+JNI",
    "--enable-http",
    "--enable-https",
    "--install-exit-handlers",
    "--no-fallback",
    "--report-unsupported-elements-at-runtime",
    "--allow-incomplete-classpath"
)

val GRAAL_CONFIGURATIONS_PATH_OPTION = { path: String -> "-H:ConfigurationFileDirectories=$path" }

val GRAAL_WINDOWS_LAUNCH_SCRIPT = { workingDirectory: Path, visualStudioVarsPath: Path, graalOptions: List<String> ->
    """
            cmd.exe /c "call `"${visualStudioVarsPath.toFile().absolutePath}`" && set > ${workingDirectory.resolve("vcvars.environment").toAbsolutePath()}"
            Get-Content "${workingDirectory.resolve("vcvars.environment").toAbsolutePath()}" | Foreach-Object { 
                if (${DOLLAR}_-match "^(.*?)=(.*)$DOLLAR") { 
                    Set-Content "env:\$DOLLAR(${DOLLAR}matches[1])"${DOLLAR}matches[2] 
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
    X64("x64"),
    ARM("aarch64")
}

enum class GraalAgentOutputMode {
    OVERWRITE,
    MERGE
}

const val GRAAL_LLVM_OPTION = "-H:CompilerBackend=llvm"
const val GRAAL_MUSL_OPTION = "--libc=musl"
const val GRAAL_EPSILON_OPTION = "--gc=epsilon"
const val GRAAL_G1_OPTION = "--gc=g1"
const val GRAAL_SERIAL_OPTION = "--GC=serial"
const val GRAAL_STATIC_OPTION = "--static"
const val GRAAL_VERBOSE_OPTION = "--verbose"
const val GRAAL_NATIVE_IMAGE_INFO_OPTION = "--native-image-info"

val GRAAL_AGENT_OUTPUT_DIR_OPTION = { path: Path -> "config-output-dir=${path.toAbsolutePath()}" }
val GRAAL_AGENT_MERGE_DIR_OPTION = { path: Path -> "config-merge-dir=${path.toAbsolutePath()}" }
val GRAAL_AGENT_WRITE_PERIOD_OPTION = { seconds: Long -> "config-write-period-secs=$seconds" }
val GRAAL_AGENT_WRITE_INITIAL_DELAY_OPTION = { seconds: Long -> "config-write-initial-delay-secs=$seconds" }
val GRAAL_AGENT_ACCESS_FILTER_OPTION = { path: Path -> "access-filter-file=${path.toAbsolutePath()}" }
val GRAAL_AGENT_CALLER_FILTER_OPTION = { path: Path -> "caller-filter-file=${path.toAbsolutePath()}" }
const val GRAAL_NATIVE_IMAGE_AGENT_OPTION = "-agentlib:native-image-agent"

val GRAAL_DOWNLOAD_TIMEOUT: Duration = ofMinutes(10)

const val GRAAL_WINDOWS_VISUAL_STUDIO_VARS_PROPERTY = "graalWindowsVisualStudioVars"
const val GRAAL_WINDOWS_VISUAL_STUDIO_VARS_ENVIRONMENT = "GRAAL_WINDOWS_VC_VARS_PATH"
const val GRAAL_OPTIONS_PROPERTY = "native-options"

const val BUILD_NATIVE_EXECUTABLE_TASK = "build-native-executable"
const val BUILD_NATIVE_TEST_TASK = "build-native-test"
const val RUN_NATIVE_EXECUTABLE_TASK = "run-native-executable"
const val RUN_NATIVE_TEST_TASK = "run-native-test"
const val RUN_NATIVE_AGENT = "run-native-agent"
const val RUN_NATIVE_TEST_AGENT = "run-native-test-agent"

val GRAAL_MAC_OS_BIN_PATH: Path = Paths.get("Contents").resolve("Home").resolve("bin")

val SYSTEM_PROPERTY = { name: String, value: String -> "-D$name=$value" }

const val GRAAL_NETTY_STATIC_LINK_PROPERTY = "netty-static"
const val GRAAL_WORKING_PATH_PROPERTY = "working-path"
