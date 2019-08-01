package ru.art.configuration

import org.gradle.api.*
import org.gradle.api.model.*
import ru.art.constants.*
import javax.inject.*

open class ProjectConfiguration @Inject constructor(objectFactory: ObjectFactory, val project: Project) {
    var externalDependencyVersionsConfiguration = objectFactory.newInstance(ExternalDependencyVersionsConfiguration::class.java)
        private set
    var repositoryConfiguration = objectFactory.newInstance(RepositoryConfiguration::class.java)
        private set
    var javaConfiguration = objectFactory.newInstance(JavaConfiguration::class.java)
        private set
    var checkstyleConfiguration = objectFactory.newInstance(CheckstyleConfiguration::class.java)
        private set
    var dependencySubstitutionConfiguration = objectFactory.newInstance(DependencySubstitutionConfiguration::class.java, project)
        private set
    var publishingConfiguration = objectFactory.newInstance(PublishingConfiguration::class.java, project)
        private set
    var testConfiguration = objectFactory.newInstance(TestConfiguration::class.java)
        private set
    var providedModulesConfiguration = objectFactory.newInstance(ModulesCombinationConfiguration::class.java, project)
        private set
    var embeddedModulesConfiguration = objectFactory.newInstance(ModulesCombinationConfiguration::class.java, project)
        private set
    var testModulesConfiguration = objectFactory.newInstance(ModulesCombinationConfiguration::class.java, project)
        private set
    var projectVersionConfiguration = objectFactory.newInstance(VersionConfiguration::class.java)
        private set
    var dependencyVersionsConfiguration = objectFactory.newInstance(DependencyVersionsConfiguration::class.java, project)
        private set
    var dependencyRefreshingConfiguration = objectFactory.newInstance(DependencyRefreshingConfiguration::class.java)
        private set
    var generatorConfiguration = objectFactory.newInstance(GeneratorConfiguration::class.java, project)
        private set
    var resourcesConfiguration = objectFactory.newInstance(ResourcesConfiguration::class.java)
        private set
    var withLombok = true
        private set
    var withProtobufGenerator = false
        private set
    var withSpock: Boolean? = null
        private set
    var withCheckstyle: Boolean? = null
        private set
    var withGatling: Boolean? = null
        private set
    var withJmh: Boolean? = null
        private set
    var withKotlin: Boolean? = null
        private set
    var withScala: Boolean? = null
        private set
    var withGroovy: Boolean? = null
        private set
    var withWeb: Boolean? = null
        private set
    var useProGuard = false
        private set
    var mainClass = EMPTY_STRING
        private set

    fun repository(action: Action<in RepositoryConfiguration>) = action.execute(repositoryConfiguration)

    fun dependencyVersions(action: Action<in DependencyVersionsConfiguration>) = action.execute(dependencyVersionsConfiguration)

    fun dependencyRefreshing(action: Action<in DependencyRefreshingConfiguration>) = action.execute(dependencyRefreshingConfiguration)

    fun externalDependencyVersions(action: Action<in ExternalDependencyVersionsConfiguration>) = action.execute(externalDependencyVersionsConfiguration)

    fun java(action: Action<in JavaConfiguration>) = action.execute(javaConfiguration)

    fun resources(action: Action<in ResourcesConfiguration>) = action.execute(resourcesConfiguration)

    fun publishing(action: Action<in PublishingConfiguration>) = action.execute(publishingConfiguration)

    fun test(action: Action<in TestConfiguration>) = action.execute(testConfiguration)

    fun checkstyle(action: Action<in CheckstyleConfiguration>) {
        action.execute(checkstyleConfiguration)
        withCheckstyle = true
    }

    fun dependencySubstitution(action: Action<in DependencySubstitutionConfiguration>) = action.execute(dependencySubstitutionConfiguration)

    fun embeddedModules(action: Action<in ModulesCombinationConfiguration>) {
        action.execute(embeddedModulesConfiguration)
    }

    fun providedModules(action: Action<in ModulesCombinationConfiguration>) {
        action.execute(providedModulesConfiguration)
    }

    fun testModules(action: Action<in ModulesCombinationConfiguration>) {
        action.execute(testModulesConfiguration)
    }

    fun projectVersion(action: Action<in VersionConfiguration>) {
        action.execute(projectVersionConfiguration)
    }

    fun generator(action: Action<in GeneratorConfiguration>) {
        action.execute(generatorConfiguration)
    }

    fun withoutLombok() {
        withLombok = false
    }

    fun withProtobufGenerator() {
        withProtobufGenerator = true
    }

    fun withSpockFramework() {
        withSpock = true
    }

    fun withCheckstyle() {
        withCheckstyle = true
    }

    fun withGatling() {
        withGatling = true
    }

    fun withJmh() {
        withJmh = true
    }

    fun withKotlin() {
        withKotlin = true
    }

    fun withScala() {
        withScala = true
    }

    fun withGroovy() {
        withGroovy = true
    }

    fun withWeb() {
        withWeb = true
    }

    fun enableProGuard() {
        useProGuard = true
    }

    fun mainClass(mainClassName: String) {
        mainClass = mainClassName
    }
}