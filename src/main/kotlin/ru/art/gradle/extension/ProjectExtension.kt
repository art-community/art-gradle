/*
 * ART Java
 *
 * Copyright 2019 ART
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

package ru.art.gradle.extension

import org.gradle.api.*
import org.gradle.api.model.*
import ru.art.gradle.configuration.*
import ru.art.gradle.constants.*
import javax.inject.*

open class ProjectExtension @Inject constructor(objectFactory: ObjectFactory, val project: Project) {
    var gatlingConfiguration = objectFactory.newInstance(GatlingConfiguration::class.java, objectFactory, project)
        private set
    var groovyConfiguration = objectFactory.newInstance(GroovyConfiguration::class.java)
        private set
    var javaConfiguration = objectFactory.newInstance(JavaConfiguration::class.java)
        private set
    var ideaConfiguration = objectFactory.newInstance(IdeaConfiguration::class.java)
        private set
    var jmhConfiguration = objectFactory.newInstance(JmhConfiguration::class.java)
        private set
    var kotlinConfiguration = objectFactory.newInstance(KotlinConfiguration::class.java)
        private set
    var lombokConfiguration = objectFactory.newInstance(LombokConfiguration::class.java)
        private set
    var protobufGeneratorConfiguration = objectFactory.newInstance(ProtobufGeneratorConfiguration::class.java)
        private set
    var scalaConfiguration = objectFactory.newInstance(ScalaConfiguration::class.java)
        private set
    var spockFrameworkConfiguration = objectFactory.newInstance(SpockFrameworkConfiguration::class.java)
        private set
    var webConfiguration = objectFactory.newInstance(WebConfiguration::class.java)
        private set
    var checkstyleConfiguration = objectFactory.newInstance(CheckstyleConfiguration::class.java)
        private set
    var testsConfiguration = objectFactory.newInstance(TestsConfiguration::class.java)
        private set
    var projectVersionConfiguration = objectFactory.newInstance(VersionConfiguration::class.java)
        private set
    var dependencyRefreshingConfiguration = objectFactory.newInstance(DependencyRefreshingConfiguration::class.java)
        private set

    var providedModulesConfiguration = objectFactory.newInstance(ModulesCombinationConfiguration::class.java, project)
        private set
    var embeddedModulesConfiguration = objectFactory.newInstance(ModulesCombinationConfiguration::class.java, project)
        private set
    var testModulesConfiguration = objectFactory.newInstance(ModulesCombinationConfiguration::class.java, project)
        private set

    var resourcesConfiguration = objectFactory.newInstance(ResourcesConfiguration::class.java)
        private set
    var externalDependencyVersionsConfiguration = objectFactory.newInstance(ExternalDependencyVersionsConfiguration::class.java)
        private set

    var dependencySubstitutionConfiguration = objectFactory.newInstance(DependencySubstitutionConfiguration::class.java, project)
        private set

    var mainClass = EMPTY_STRING
        private set
    var generatorConfiguration = objectFactory.newInstance(GeneratorConfiguration::class.java, project)
        private set

    val versions = objectFactory.newInstance(VersioningConfiguration::class.java)

    fun gatling(action: Action<in GatlingConfiguration> = Action {}) {
        gatlingConfiguration.enabled = true
        action.execute(gatlingConfiguration)
    }

    fun groovy(action: Action<in GroovyConfiguration> = Action {}) {
        groovyConfiguration.enabled = true
        action.execute(groovyConfiguration)
    }

    fun idea(action: Action<in IdeaConfiguration> = Action {}) {
        ideaConfiguration.enabled = true
        action.execute(ideaConfiguration)
    }

    fun jmh(action: Action<in JmhConfiguration> = Action {}) {
        jmhConfiguration.enabled = true
        action.execute(jmhConfiguration)
    }

    fun java(action: Action<in JavaConfiguration> = Action {}) {
        action.execute(javaConfiguration)
    }

    fun kotlin(action: Action<in KotlinConfiguration> = Action {}) {
        kotlinConfiguration.enabled = true
        action.execute(kotlinConfiguration)
    }

    fun lombok(action: Action<in LombokConfiguration> = Action {}) {
        lombokConfiguration.enabled = true
        action.execute(lombokConfiguration)
    }

    fun protobufGenerator(action: Action<in ProtobufGeneratorConfiguration> = Action {}) {
        protobufGeneratorConfiguration.enabled = true
        action.execute(protobufGeneratorConfiguration)
    }

    fun scala(action: Action<in ScalaConfiguration> = Action {}) {
        scalaConfiguration.enabled = true
        action.execute(scalaConfiguration)
    }

    fun spockFramework(action: Action<in SpockFrameworkConfiguration> = Action {}) {
        spockFrameworkConfiguration.enabled = true
        action.execute(spockFrameworkConfiguration)
    }

    fun web(action: Action<in WebConfiguration> = Action {}) {
        webConfiguration.enabled = true
        action.execute(webConfiguration)
    }

    fun checkstyle(action: Action<in CheckstyleConfiguration> = Action {}) {
        checkstyleConfiguration.enabled = true
        action.execute(checkstyleConfiguration)
    }

    fun tests(action: Action<in TestsConfiguration> = Action {}) {
        testsConfiguration.enabled = true
        action.execute(testsConfiguration)
    }

    fun projectVersion(action: Action<in VersionConfiguration>) {
        action.execute(projectVersionConfiguration)
    }

    fun dependencyRefreshing(action: Action<in DependencyRefreshingConfiguration> = Action {}) {
        dependencyRefreshingConfiguration.enabled = true
        action.execute(dependencyRefreshingConfiguration)
    }


    fun embeddedModules(action: Action<in ModulesCombinationConfiguration>) {
        action.execute(embeddedModulesConfiguration)
    }

    fun providedModules(action: Action<in ModulesCombinationConfiguration>) {
        action.execute(providedModulesConfiguration)
    }

    fun testModules(action: Action<in ModulesCombinationConfiguration>) {
        action.execute(testModulesConfiguration)
    }

    fun resources(action: Action<in ResourcesConfiguration>) = action.execute(resourcesConfiguration)

    fun externalDependencyVersions(action: Action<in ExternalDependencyVersionsConfiguration>) = action.execute(externalDependencyVersionsConfiguration)


    fun dependencySubstitution(action: Action<in DependencySubstitutionConfiguration>) = action.execute(dependencySubstitutionConfiguration)


    fun generator(action: Action<in GeneratorConfiguration>) {
        action.execute(generatorConfiguration)
    }

    fun mainClass(mainClassName: String) {
        mainClass = mainClassName
    }
}