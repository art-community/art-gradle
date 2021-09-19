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

import io.art.gradle.common.constants.API_CONFIGURATION_NAME
import io.art.gradle.common.constants.EMBEDDED_CONFIGURATION_NAME
import io.art.gradle.common.constants.IMPLEMENTATION_CONFIGURATION_NAME
import io.art.gradle.external.constants.ArtVersion.MAIN
import io.art.gradle.external.constants.JavaModules
import io.art.gradle.external.constants.JavaModules.*
import io.art.gradle.external.constants.KotlinModules
import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectSet
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.namedDomainObjectSet
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

open class ModulesConfiguration @Inject constructor(private val objectFactory: ObjectFactory) {
    val dependencies: NamedDomainObjectSet<ModuleDependenciesConfiguration> = objectFactory.namedDomainObjectSet(ModuleDependenciesConfiguration::class).apply {
        add(objectFactory.newInstance(IMPLEMENTATION_CONFIGURATION_NAME))
        add(objectFactory.newInstance(EMBEDDED_CONFIGURATION_NAME))
        add(objectFactory.newInstance(API_CONFIGURATION_NAME))
    }

    var version = MAIN.version
        private set

    fun api(action: Action<in ModuleDependenciesConfiguration>) {
        dependencies.named(API_CONFIGURATION_NAME, action)
    }

    fun implementation(action: Action<in ModuleDependenciesConfiguration>) {
        dependencies.named(IMPLEMENTATION_CONFIGURATION_NAME, action)
    }

    fun embedded(action: Action<in ModuleDependenciesConfiguration>) {
        dependencies.named(EMBEDDED_CONFIGURATION_NAME, action)
    }

    fun version(version: String) {
        this.version = version
    }

    open class ModuleDependenciesConfiguration @Inject constructor(objectFactory: ObjectFactory, private val name: String) : Named {
        override fun getName(): String = name

        val java: JavaModulesConfiguration = objectFactory.newInstance(JavaModulesConfiguration::class.java)
        val kotlin: KotlinModulesConfiguration = objectFactory.newInstance(KotlinModulesConfiguration::class.java)

        fun java(action: Action<in JavaModulesConfiguration>) {
            action.execute(java)
        }

        fun kotlin(action: Action<in KotlinModulesConfiguration>) {
            action.execute(kotlin)
        }
    }

    open class JavaModulesConfiguration @Inject constructor() {
        val modules = mutableSetOf<JavaModules>()

        fun kit() {
            modules.addAll(JavaModules.values())
        }

        fun core() {
            modules.add(CORE)
        }

        fun logging() {
            modules.add(LOGGING)
        }

        fun scheduler() {
            modules.add(SCHEDULER)
        }

        fun configurator() {
            modules.add(CONFIGURATOR)
        }

        fun json() {
            modules.add(JSON)
        }

        fun messagePack() {
            modules.add(MESSAGE_PACK)
        }

        fun yaml() {
            modules.add(YAML)
        }

        fun transport() {
            modules.add(TRANSPORT)
        }

        fun http() {
            modules.add(HTTP)
        }

        fun rsocket() {
            modules.add(RSOCKET)
        }

        fun tarantool() {
            modules.add(TARANTOOL)
        }

        fun meta() {
            modules.add(META)
        }

        fun launcher() {
            modules.add(LAUNCHER)
        }

        fun tests() {
            modules.add(TESTS)
        }
    }

    open class KotlinModulesConfiguration @Inject constructor() {
        val modules = mutableSetOf<KotlinModules>()

        fun kit() {
            modules.addAll(KotlinModules.values())
        }

        fun core() {
            modules.add(KotlinModules.CORE)
        }

        fun logging() {
            modules.add(KotlinModules.LOGGING)
        }

        fun scheduler() {
            modules.add(KotlinModules.SCHEDULER)
        }

        fun configurator() {
            modules.add(KotlinModules.CONFIGURATOR)
        }

        fun json() {
            modules.add(KotlinModules.JSON)
        }

        fun messagePack() {
            modules.add(KotlinModules.MESSAGE_PACK)
        }

        fun yaml() {
            modules.add(KotlinModules.YAML)
        }

        fun rsocket() {
            modules.add(KotlinModules.RSOCKET)
        }

        fun http() {
            modules.add(KotlinModules.HTTP)
        }

        fun meta() {
            modules.add(KotlinModules.META)
        }

        fun transport() {
            modules.add(KotlinModules.TRANSPORT)
        }

        fun launcher() {
            modules.add(KotlinModules.LAUNCHER)
        }

        fun tests() {
            modules.add(KotlinModules.TESTS)
        }
    }
}
