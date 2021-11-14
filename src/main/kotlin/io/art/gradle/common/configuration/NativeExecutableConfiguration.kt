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

package io.art.gradle.common.configuration

import io.art.gradle.common.constants.*
import io.art.gradle.common.constants.GraalAgentOutputMode.MERGE
import io.art.gradle.common.constants.GraalAgentOutputMode.OVERWRITE
import io.art.gradle.common.constants.ProcessorArchitectures.ARM_V8
import io.art.gradle.common.constants.ProcessorArchitectures.X86_64
import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.JavaVersion.current
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.JavaExec
import org.gradle.internal.os.OperatingSystem
import org.gradle.kotlin.dsl.newInstance
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration
import javax.inject.Inject

open class NativeExecutableConfiguration @Inject constructor(objectFactory: ObjectFactory) {
    val agentConfiguration: NativeImageAgentConfiguration = objectFactory.newInstance()

    var enableAgent = false
        private set

    var runAgentBeforeBuild = false
        private set

    var graalVersion: String = GraalVersion.VERSION_21_2_0.version
        private set

    var graalUrl: String? = null
        private set

    var graalJavaVersion: GraalJavaVersion = when {
        current().isCompatibleWith(JavaVersion.VERSION_1_9) -> GraalJavaVersion.JAVA_11
        else -> GraalJavaVersion.JAVA_8
    }

    var graalPlatform: GraalPlatformName = when {
        OperatingSystem.current().isWindows -> GraalPlatformName.WINDOWS
        OperatingSystem.current().isLinux -> GraalPlatformName.LINUX
        OperatingSystem.current().isMacOsX -> GraalPlatformName.DARWIN
        else -> throw unsupportedGraalOs(OperatingSystem.current())
    }

    var graalArchitecture: GraalArchitectureName = System.getProperty(OS_ARCH_PROPERTY).let { architecture ->
        when {
            X86_64.architecture.names().any(architecture::contains) -> GraalArchitectureName.AMD
            ARM_V8.architecture.names().any(architecture::contains) -> GraalArchitectureName.ARM
            else -> throw unsupportedGraalArchitecture(architecture)
        }
    }

    var graalDirectory: Path? = null
        private set

    var graalOptions: MutableList<String> = GRAAL_DEFAULT_OPTIONS.toMutableList()
        private set

    var graalOptionsReplacer: (current: List<String>) -> List<String> = { it }
        private set

    var graalWindowsVcVarsPath: Path? = null
        private set

    var graalSystemProperties = mutableMapOf<String, String>()
        private set

    var llvm = false
        private set

    var runConfigurator: Exec.() -> Unit = {}
        private set

    var buildConfigurator: Exec.() -> Unit = {}
        private set

    fun graalWindowsVisualStudioVars(absoluteScriptPath: String) {
        graalWindowsVcVarsPath = Paths.get(absoluteScriptPath)
    }

    fun graalVersion(version: String) {
        this.graalVersion = version
    }

    fun graalUrl(url: String) {
        this.graalUrl = url
    }

    fun graalVersion(version: GraalVersion) {
        this.graalVersion = version.version
    }

    fun graalJavaVersion(version: GraalJavaVersion) {
        this.graalJavaVersion = version
    }

    fun graalPlatform(platformName: GraalPlatformName) {
        this.graalPlatform = platformName
    }

    fun graalArchitecture(architectureName: GraalArchitectureName) {
        this.graalArchitecture = architectureName
    }

    fun graalDirectory(directory: Path) {
        this.graalDirectory = directory
    }

    fun replaceGraalOptions(options: (current: List<String>) -> List<String>) {
        this.graalOptionsReplacer = options
    }

    fun addGraalOptions(vararg options: String) {
        this.graalOptions.addAll(options)
    }

    fun graalWindowsVcVarsPath() {
        this.graalWindowsVcVarsPath = graalWindowsVcVarsPath
    }

    fun addGraalSystemProperty(name: String, value: String) {
        graalSystemProperties[name] = value
    }

    fun graalSystemProperties(properties: Map<String, String>) {
        graalSystemProperties.putAll(properties)
    }

    fun llvm(use: Boolean = true) {
        llvm = use

        if (use) {
            graalOptions.add(GRAAL_LLVM_OPTION)
            return
        }

        graalOptions.remove(GRAAL_LLVM_OPTION)
    }

    fun musl(use: Boolean = true) {
        if (use) {
            graalOptions.add(GRAAL_MUSL_OPTION)
            return
        }
        graalOptions.remove(GRAAL_MUSL_OPTION)
    }

    fun static(static: Boolean = true) {
        if (static) {
            graalOptions.add(GRAAL_STATIC_OPTION)
            return
        }
        graalOptions.remove(GRAAL_STATIC_OPTION)
    }

    fun verbose(verbose: Boolean = true) {
        if (verbose) {
            graalOptions.add(GRAAL_VERBOSE_OPTION)
            graalOptions.add(GRAAL_NATIVE_IMAGE_INFO_OPTION)
            return
        }
        graalOptions.remove(GRAAL_VERBOSE_OPTION)
        graalOptions.remove(GRAAL_NATIVE_IMAGE_INFO_OPTION)
    }

    fun agent(runBeforeNativeBuild: Boolean = false, action: Action<in NativeImageAgentConfiguration> = Action {}) {
        enableAgent = true
        runAgentBeforeBuild = runBeforeNativeBuild
        action.execute(agentConfiguration)
    }

    fun configureRun(runConfigurator: Exec.() -> Unit) {
        this.runConfigurator = runConfigurator
    }

    fun configureBuild(buildConfigurator: Exec.() -> Unit) {
        this.buildConfigurator = buildConfigurator
    }


    open class NativeImageAgentConfiguration {
        var executableClass: String? = null
            private set

        var configurationPath: Path? = null
            private set

        var outputMode: GraalAgentOutputMode = OVERWRITE
            private set

        var configurationWritePeriod: Duration? = null
            private set

        var configurationWriteInitialDelay: Duration? = null
            private set

        var accessFilter: Path? = null
            private set

        var callerFilter: Path? = null
            private set

        var agentOptions = mutableListOf<String>()
            private set

        var agentOptionsReplacer: (current: List<String>) -> List<String> = { it }
            private set

        var runConfigurator: JavaExec.() -> Unit = {}
            private set

        fun output(path: Path? = null) {
            configurationPath = path
            outputMode = OVERWRITE
        }

        fun merge(path: Path? = null) {
            configurationPath = path
            outputMode = MERGE
        }

        fun writePeriod(duration: Duration) {
            configurationWritePeriod = duration
        }

        fun writeInitialDelay(duration: Duration) {
            configurationWriteInitialDelay = duration
        }

        fun executableClass(name: String) {
            executableClass = name
        }

        fun replaceAgentOptions(options: (current: List<String>) -> List<String>) {
            agentOptionsReplacer = options
        }

        fun addAgentOptions(vararg options: String) {
            this.agentOptions.addAll(options)
        }

        fun accessFilter(file: Path) {
            accessFilter = file
        }

        fun callerFilter(file: Path) {
            callerFilter = file
        }

        fun configureRun(configurator: JavaExec.() -> Unit) {
            runConfigurator = configurator
        }

    }
}
