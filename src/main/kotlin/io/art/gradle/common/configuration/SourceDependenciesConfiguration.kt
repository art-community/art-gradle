import CmakeSourceDependency.BuildType.*
import io.art.gradle.common.constants.*
import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.internal.os.OperatingSystem
import org.gradle.kotlin.dsl.domainObjectContainer
import java.io.File
import java.nio.file.Path
import javax.inject.Inject

open class SourceDependenciesConfiguration @Inject constructor(project: Project, objectFactory: ObjectFactory) {
    var directory: Path = project.layout.buildDirectory.file(DEPENDENCIES).get().asFile.toPath()
        private set
    val unixDependencies: NamedDomainObjectContainer<UnixSourceDependency> = objectFactory.domainObjectContainer(UnixSourceDependency::class, ::UnixSourceDependency)
    val cmakeDependencies: NamedDomainObjectContainer<CmakeSourceDependency> = objectFactory.domainObjectContainer(CmakeSourceDependency::class, ::CmakeSourceDependency)

    fun directory(directory: Path) {
        this.directory = directory
    }

    fun unix(name: String, action: Action<in UnixSourceDependency>) {
        action.execute(unixDependencies.create(name))
    }

    fun cmake(name: String, action: Action<in CmakeSourceDependency>) {
        action.execute(cmakeDependencies.create(name))
    }

    fun lxc(static: Boolean = true, action: Action<in UnixSourceDependency> = Action {}) {
        val lxc = preconfiguredLxc(static)
        action.execute(lxc)
        unixDependencies.add(lxc)
    }
}

interface SourceDependency {
    fun builtFiles(): Map<String, String>
}

open class UnixSourceDependency @Inject constructor(private val name: String) : Named, SourceDependency {
    private val autogenOptions: MutableList<String> = mutableListOf()

    private val configureOptions: MutableList<String> = mutableListOf()

    private val makeOptions: MutableList<String> = mutableListOf()

    private val builtFiles: MutableMap<String, String> = mutableMapOf()

    var beforeBuild = false
        private set

    var url: String? = null
        private set

    var version: String? = null
        private set

    fun url(url: String) {
        this.url = url
    }

    fun version(version: String) {
        this.version = version
    }

    fun autogenOptions(vararg options: String) {
        autogenOptions += options
    }

    fun configureOptions(vararg options: String) {
        configureOptions += options
    }

    fun makeOptions(vararg options: String) {
        makeOptions += options
    }

    fun parallel(cores: Int = Runtime.getRuntime().availableProcessors()) {
        makeOptions += "-j $cores"
    }

    fun copy(from: String, to: String) {
        builtFiles += from to to
    }

    fun copy(from: Path, to: Path) {
        builtFiles += from.toString() to to.toString()
    }

    fun copy(from: File, to: File) {
        builtFiles += from.toString() to to.toString()
    }

    fun beforeBuild(before: Boolean = true) {
        beforeBuild = before
    }

    fun autogenCommand(): Array<String> = bashCommand(AUTOGEN_SCRIPT, autogenOptions.joinToString(SPACE))

    fun configureCommand(): Array<String> = bashCommand(CONFIGURE_SCRIPT, configureOptions.joinToString(SPACE))

    fun makeCommand(): Array<String> = bashCommand(MAKE, makeOptions.joinToString(SPACE))

    override fun builtFiles() = builtFiles

    override fun getName(): String = name
}

open class CmakeSourceDependency @Inject constructor(private val name: String) : Named, SourceDependency {
    private val cmakeConfigureOptions: MutableList<String> = mutableListOf()
    private val cmakeBuildOptions: MutableList<String> = mutableListOf()
    private val builtFiles: MutableMap<String, String> = mutableMapOf()

    var wsl = false
        private set

    var buildType = RELEASE_DEBUG
        private set

    var beforeBuild = false
        private set

    var url: String? = null
        private set

    var version: String? = null
        private set

    fun url(url: String) {
        this.url = url
    }

    fun version(version: String) {
        this.version = version
    }

    fun cmakeConfigureOptions(vararg options: String) {
        cmakeConfigureOptions += options
    }

    fun cmakeBuildOptions(vararg options: String) {
        cmakeBuildOptions += options
    }

    fun parallel(cores: Int = Runtime.getRuntime().availableProcessors()) {
        cmakeBuildOptions += "-j $cores"
    }

    fun copy(from: String, to: String) {
        builtFiles += from to to
    }

    fun copy(from: Path, to: Path) {
        builtFiles += from.toString() to to.toString()
    }

    fun copy(from: File, to: File) {
        builtFiles += from.toString() to to.toString()
    }

    fun release() {
        buildType = RELEASE
    }

    fun debug() {
        buildType = DEBUG
    }

    fun releaseDebug() {
        buildType = RELEASE_DEBUG
    }

    fun beforeBuild(before: Boolean = true) {
        beforeBuild = before
    }

    fun wsl(wsl: Boolean = true) {
        if (!OperatingSystem.current().isWindows) return
        this.wsl = wsl
    }

    fun cmakeConfigureCommand(): Array<String> {
        when (buildType) {
            DEBUG -> cmakeConfigureOptions(CMAKE_BUILD_TYPE_DEBUG)
            RELEASE -> cmakeConfigureOptions(CMAKE_BUILD_TYPE_RELEASE)
            RELEASE_DEBUG -> cmakeConfigureOptions(CMAKE_BUILD_TYPE_RELEASE_WITH_DEBUG)
        }
        val command = arrayOf(CMAKE) + cmakeConfigureOptions + DOT
        if (wsl) return bashCommand(command.joinToString(SPACE))
        return command
    }

    fun cmakeBuildCommand(): Array<String> {
        when (buildType) {
            DEBUG -> cmakeBuildOptions(CMAKE_BUILD_CONFIG_OPTION, CMAKE_BUILD_CONFIG_DEBUG)
            RELEASE -> cmakeBuildOptions(CMAKE_BUILD_CONFIG_OPTION, CMAKE_BUILD_CONFIG_RELEASE)
            RELEASE_DEBUG -> cmakeBuildOptions(CMAKE_BUILD_CONFIG_OPTION, CMAKE_BUILD_CONFIG_RELEASE_WITH_DEBUG)
        }
        val command = arrayOf(CMAKE) + CMAKE_BUILD + DOT + cmakeBuildOptions
        if (wsl) return bashCommand(command.joinToString(SPACE))
        return command
    }

    override fun builtFiles() = builtFiles

    override fun getName(): String = name

    enum class BuildType(val option: String) {
        DEBUG(CMAKE_BUILD_TYPE_DEBUG),
        RELEASE(CMAKE_BUILD_TYPE_RELEASE),
        RELEASE_DEBUG(CMAKE_BUILD_TYPE_RELEASE_WITH_DEBUG)
    }
}
