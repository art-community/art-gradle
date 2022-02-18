/*
 * ART
 *
 * Copyright 2019-2022 ART
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

import SourceDependenciesConfiguration
import io.art.gradle.common.configuration.ExecutableConfiguration
import io.art.gradle.common.configuration.GeneratorConfiguration
import io.art.gradle.common.configuration.TestConfiguration
import io.art.gradle.external.plugin.externalPlugin
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

open class ExternalConfiguration @Inject constructor(objectFactory: ObjectFactory) {
    val executable = objectFactory.newInstance<ExecutableConfiguration>(externalPlugin.project)
    val test = objectFactory.newInstance<TestConfiguration>(externalPlugin.project)
    val generator = objectFactory.newInstance<GeneratorConfiguration>(externalPlugin.project)
    val modules = objectFactory.newInstance<ModulesConfiguration>()
    val libraries = objectFactory.newInstance<LibrariesConfiguration>()
    val sources = objectFactory.newInstance<SourceDependenciesConfiguration>()

    fun executable(action: Action<in ExecutableConfiguration>) {
        action.execute(executable)
    }

    fun test(action: Action<in TestConfiguration>) {
        action.execute(test)
    }

    fun generator(action: Action<in GeneratorConfiguration>) {
        action.execute(generator)
    }

    fun modules(action: Action<in ModulesConfiguration>) {
        action.execute(modules)
    }

    fun libraries(action: Action<in LibrariesConfiguration>) {
        action.execute(libraries)
    }

    fun sources(action: Action<in SourceDependenciesConfiguration>) {
        action.execute(sources)
    }
}

