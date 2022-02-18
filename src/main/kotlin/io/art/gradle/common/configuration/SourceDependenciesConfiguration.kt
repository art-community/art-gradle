import io.art.gradle.common.constants.*
import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.domainObjectContainer
import java.io.File
import java.nio.file.Path
import javax.inject.Inject

open class SourceDependenciesConfiguration @Inject constructor(project: Project, objectFactory: ObjectFactory) {
    var directory: Path = project.buildDir.resolve(DEPENDENCIES).toPath()
        private set
    val unixDependencies: NamedDomainObjectContainer<UnixSourceDependency> = objectFactory.domainObjectContainer(UnixSourceDependency::class, ::UnixSourceDependency)

    fun directory(directory: Path) {
        this.directory = directory
    }

    fun unix(name: String, action: Action<in UnixSourceDependency>) {
        action.execute(unixDependencies.create(name))
    }

    fun lxc(action: Action<in UnixSourceDependency> = Action {}) {
        val lxc = builtinLxc()
        action.execute(lxc)
        unixDependencies.add(lxc)
    }
}

open class UnixSourceDependency @Inject constructor(private val name: String) : Named {
    private val autogenOptions: MutableList<String> = mutableListOf()
    private val configureOptions: MutableList<String> = mutableListOf()
    private val makeOptions: MutableList<String> = mutableListOf()
    private val builtFiles: MutableMap<String, String> = mutableMapOf()

    var buildDependency = false
        private set

    var url: String? = null
        private set

    fun url(url: String) {
        this.url = url
    }

    fun autogenOptions(vararg options: String) {
        autogenOptions += options
    }

    fun configureOptions(vararg options: String) {
        configureOptions += options
    }

    fun makeOptions(vararg options: String) {
        makeOptions + options
    }

    fun parallel(cores: Int = Runtime.getRuntime().availableProcessors()) {
        makeOptions + "-j $cores"
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

    fun buildDependency(buildDependency: Boolean = true) {
        this.buildDependency = buildDependency
    }

    fun autogenCommand(): Array<String> = bashCommand(AUTOGEN_SCRIPT, autogenOptions.joinToString(SPACE))

    fun configureCommand(): Array<String> = bashCommand(CONFIGURE_SCRIPT, configureOptions.joinToString(SPACE))

    fun makeCommand(): Array<String> = bashCommand(MAKE, makeOptions.joinToString(SPACE))

    fun builtFiles() = builtFiles

    override fun getName(): String = name
}
