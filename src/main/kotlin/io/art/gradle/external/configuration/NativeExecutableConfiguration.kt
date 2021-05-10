package io.art.gradle.external.configuration

import io.art.gradle.external.constants.*
import io.art.gradle.external.constants.GraalAgentOutputMode.MERGE
import io.art.gradle.external.constants.GraalAgentOutputMode.OVERWRITE
import io.art.gradle.external.constants.ProcessorArchitectures.ARM_V8
import io.art.gradle.external.constants.ProcessorArchitectures.X86_64
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

    var graalVersion: String = GraalVersion.LATEST.version
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

    var graalConfigurationDirectory: Path? = null
        private set

    var graalOptions: MutableList<String> = GRAAL_MANDATORY_OPTIONS.toMutableList()
        private set

    var graalWindowsVcVarsPath: Path? = null
        private set

    var llvm = false
        private set

    var runConfigurator: Exec.() -> Unit = {}
        private set

    var buildConfigurator: Exec.() -> Unit = {}
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

    fun graalConfigurationDirectory(directory: String) {
        this.graalConfigurationDirectory = Paths.get(directory)
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

    fun agent(action: Action<in NativeImageAgentConfiguration> = Action {}) {
        enableAgent = true
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

        var outputMode: GraalAgentOutputMode = MERGE
            private set

        var configurationWritePeriod: Duration? = null
            private set

        var configurationWriteInitialDelay: Duration? = null
            private set

        var runConfigurator: JavaExec.() -> Unit = {}
            private set

        fun output(path: String) {
            configurationPath = Paths.get(path)
            outputMode = OVERWRITE
        }

        fun merge(path: String) {
            configurationPath = Paths.get(path)
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

        fun configureRun(configurator: JavaExec.() -> Unit) {
            runConfigurator = configurator
        }
    }
}