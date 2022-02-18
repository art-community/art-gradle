import io.art.gradle.common.constants.*
import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.domainObjectContainer
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

    fun lxc() {
        unixDependencies.add(LCX)
    }
}

open class UnixSourceDependency @Inject constructor(private val name: String) : Named {
    private val autogenOptions: MutableList<String> = mutableListOf()
    private val configureOptions: MutableList<String> = mutableListOf()
    private val makeOptions: MutableList<String> = mutableListOf()
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

    fun autogenCommand(): Array<String> = bashCommand(arrayOf(AUTOGEN_SCRIPT, autogenOptions.joinToString(SPACE)))

    fun configureCommand(): Array<String> = bashCommand(arrayOf(CONFIGURE_SCRIPT, configureOptions.joinToString(SPACE)))

    fun makeCommand(): Array<String> = bashCommand(arrayOf(MAKE, makeOptions.joinToString(SPACE)))

    override fun getName(): String = name
}
