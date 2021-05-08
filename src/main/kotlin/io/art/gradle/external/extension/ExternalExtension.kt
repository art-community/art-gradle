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

package io.art.gradle.external.extension

import io.art.gradle.external.configuration.ExecutableConfiguration
import io.art.gradle.external.configuration.GeneratorConfiguration
import io.art.gradle.external.configuration.ModulesConfiguration
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

open class ExternalExtension @Inject constructor(objectFactory: ObjectFactory) {
    val executable = objectFactory.newInstance<ExecutableConfiguration>()
    val generator = objectFactory.newInstance<GeneratorConfiguration>()
    val modules = objectFactory.newInstance<ModulesConfiguration>()

    fun executable(action: Action<in ExecutableConfiguration>) {
        action.execute(executable)
    }

    fun generator(action: Action<in GeneratorConfiguration>) {
        action.execute(generator)
    }

    fun modules(action: Action<in ModulesConfiguration>) {
        action.execute(modules)
    }
}

