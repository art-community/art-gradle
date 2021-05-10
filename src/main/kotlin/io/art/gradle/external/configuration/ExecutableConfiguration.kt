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

package io.art.gradle.external.configuration

import io.art.gradle.external.constants.*
import io.art.gradle.external.constants.GraalArchitectureName.AMD
import io.art.gradle.external.constants.GraalArchitectureName.ARM
import io.art.gradle.external.constants.ProcessorArchitectures.ARM_V8
import io.art.gradle.external.constants.ProcessorArchitectures.X86_64
import io.art.gradle.external.plugin.externalPlugin
import org.gradle.api.Action
import org.gradle.api.JavaVersion.VERSION_1_9
import org.gradle.api.JavaVersion.current
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.DuplicatesStrategy.EXCLUDE
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Exec
import org.gradle.internal.os.OperatingSystem
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.newInstance
import org.gradle.process.JavaExecSpec
import java.nio.file.Path
import java.nio.file.Paths
import javax.inject.Inject

open class ExecutableConfiguration @Inject constructor(objectFactory: ObjectFactory) {
    var mainClass: String? = null
        private set
    var executableName: String = externalPlugin.project.name
        private set
    var directory: Path = externalPlugin.project.buildDir.resolve(EXECUTABLE).toPath()
        private set

    val jar = objectFactory.newInstance<JarExecutableConfiguration>()

    val native = objectFactory.newInstance<NativeExecutableConfiguration>()

    var jarEnabled = false
        private set

    var nativeEnabled = false
        private set


    fun name(name: String) {
        this.executableName = name
    }

    fun directory(directory: Path) {
        this.directory = directory
    }

    fun jar(action: Action<in JarExecutableConfiguration> = Action { }) {
        action.execute(jar)
        jarEnabled = true
    }

    fun native(action: Action<in NativeExecutableConfiguration> = Action { }) {
        action.execute(native)
        nativeEnabled = true
    }

    fun main(mainClass: String) {
        this.mainClass = mainClass
    }

    open class JarExecutableConfiguration @Inject constructor() {
        var duplicateStrategy: DuplicatesStrategy = EXCLUDE
            private set
        var multiRelease = current().isCompatibleWith(VERSION_1_9)
            private set
        var runConfigurator: JavaExecSpec.() -> Unit = {}
            private set
        var buildConfigurator: Jar.() -> Unit = {}
            private set
        var asBuildDependency: Boolean = true
            private set

        var manifestAdditionalAttributes = mutableMapOf<String, String>()
            private set

        var exclusions = MANIFEST_EXCLUSIONS.toMutableSet()
            private set


        fun resolveDuplicates(strategy: DuplicatesStrategy) {
            duplicateStrategy = strategy
        }

        fun multiRelease(multiRelease: Boolean = true) {
            this.multiRelease = multiRelease
        }

        fun addManifestAttributes(attributes: Map<String, String>) {
            manifestAdditionalAttributes.putAll(attributes)
        }

        fun addManifestAttribute(name: String, value: String) {
            manifestAdditionalAttributes.put(name, value)
        }

        fun replaceManifestAttributes(attributes: (current: Map<String, String>) -> Map<String, String>) {
            manifestAdditionalAttributes = attributes(manifestAdditionalAttributes).toMutableMap()
        }

        fun addExclusions(vararg exclusions: String) {
            this.exclusions.addAll(exclusions)
        }

        fun replaceExclusions(exclusions: (current: Set<String>) -> Set<String>) {
            this.exclusions = exclusions(this.exclusions).toMutableSet()
        }

        fun buildDependsOn(buildDependsOn: Boolean = true) {
            asBuildDependency = buildDependsOn
        }

        fun configureRun(runConfigurator: JavaExecSpec.() -> Unit) {
            this.runConfigurator = runConfigurator
        }

        fun configureBuild(buildConfigurator: Jar.() -> Unit) {
            this.buildConfigurator = buildConfigurator
        }
    }

    open class NativeExecutableConfiguration {
        var runConfigurator: Exec.() -> Unit = {}
            private set
        var buildConfigurator: Exec.() -> Unit = {}
            private set
        var graalVersion: String = GraalVersion.LATEST.version
            private set

        var graalJavaVersion: GraalJavaVersion = when {
            current().isCompatibleWith(VERSION_1_9) -> GraalJavaVersion.JAVA_11
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
                X86_64.architecture.names().any(architecture::contains) -> AMD
                ARM_V8.architecture.names().any(architecture::contains) -> ARM
                else -> throw unsupportedGraalArchitecture(architecture)
            }
        }

        var graalDirectory: Path? = null
            private set

        var graalOptions: MutableList<String> = GRAAL_MANDATORY_OPTIONS.toMutableList()
            private set

        var graalWindowsVcVarsPath: Path? = null
            private set

        var llvm = false
            private set

        fun windowsVisualStudioVarsScript(script: String) {
            graalWindowsVcVarsPath = Paths.get(script)
        }

        fun graalVersion(version: String) {
            this.graalVersion = version
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

        fun graalDirectory(directory: String) {
            this.graalDirectory = Paths.get(directory)
        }

        fun replaceGraalOptions(options: (current: List<String>) -> List<String>) {
            this.graalOptions = options(graalOptions).toMutableList()
        }

        fun addGraalOptions(vararg options: String) {
            this.graalOptions.addAll(options)
        }

        fun graalWindowsVcVarsPath() {
            this.graalWindowsVcVarsPath = graalWindowsVcVarsPath
        }

        fun useLlvm(use: Boolean = true) {
            llvm = use

            if (use) {
                graalOptions.add(GRAAL_LLVM_OPTION)
                return
            }

            graalOptions.remove(GRAAL_LLVM_OPTION)
        }

        fun useMusl(use: Boolean = true) {
            if (use) {
                graalOptions.add(GRAAL_MUSL_OPTION)
                return
            }
            graalOptions.remove(GRAAL_MUSL_OPTION)
        }

        fun configureRun(runConfigurator: Exec.() -> Unit) {
            this.runConfigurator = runConfigurator
        }

        fun configureBuild(buildConfigurator: Exec.() -> Unit) {
            this.buildConfigurator = buildConfigurator
        }
    }
}
