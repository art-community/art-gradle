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

import io.art.gradle.external.constants.ArtVersion.MAIN
import io.art.gradle.external.constants.EMBEDDED_CONFIGURATION_NAME
import io.art.gradle.external.constants.IMPLEMENTATION_CONFIGURATION_NAME
import io.art.gradle.external.constants.JAVA_MODULES
import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectSet
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.namedDomainObjectSet
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

open class ModulesConfiguration @Inject constructor(private val objectFactory: ObjectFactory) {
    val modules: NamedDomainObjectSet<ModuleDependenciesConfiguration> = objectFactory.namedDomainObjectSet(ModuleDependenciesConfiguration::class).apply {
        add(objectFactory.newInstance(IMPLEMENTATION_CONFIGURATION_NAME))
        add(objectFactory.newInstance(EMBEDDED_CONFIGURATION_NAME))
    }

    var version = MAIN.version
        private set

    fun used(action: Action<in ModuleDependenciesConfiguration>) {
        modules.named(IMPLEMENTATION_CONFIGURATION_NAME, action)
    }

    fun embedded(action: Action<in ModuleDependenciesConfiguration>) {
        modules.named(EMBEDDED_CONFIGURATION_NAME, action)
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
        val modules = mutableSetOf<String>()

        fun kit() {
            modules.addAll(JAVA_MODULES)
        }

        fun core() {
            modules.add("core")
        }

        fun logging() {
            modules.add("logging")
        }

        fun value() {
            modules.add("value")
        }

        fun scheduler() {
            modules.add("scheduler")
        }

        fun configurator() {
            modules.add("configurator")
        }


        fun xml() {
            modules.add("xml")
        }

        fun json() {
            modules.add("json")
        }

        fun protobuf() {
            modules.add("protobuf")
        }

        fun messagePack() {
            modules.add("message-pack")
        }

        fun yamlConfiguration() {
            modules.add("yaml-configuration")
        }

        fun yaml() {
            modules.add("yaml")
        }


        fun resilience() {
            modules.add("resilience")
        }

        fun transport() {
            modules.add("transport")
        }

        fun server() {
            modules.add("server")
        }

        fun communicator() {
            modules.add("communicator")
        }

        fun http() {
            modules.add("http")
        }

        fun rsocket() {
            modules.add("rsocket")
        }


        fun rocksdb() {
            modules.add("rocks-db")
        }

        fun tarantool() {
            modules.add("tarantool")
        }


        fun meta() {
            modules.add("meta")
        }

        fun graal() {
            modules.add("graal")
        }

        fun storage() {
            modules.add("storage")
        }

        fun model() {
            modules.add("model")
        }

        fun launcher() {
            modules.add("launcher")
        }
    }

    open class KotlinModulesConfiguration @Inject constructor() {
        val modules = mutableSetOf<String>()
    }
}
