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

import io.art.gradle.common.constants.*
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.domainObjectSet
import javax.inject.Inject

open class LibrariesConfiguration @Inject constructor(private val objectFactory: ObjectFactory) {
    val dependencies = objectFactory.domainObjectSet(Library::class)

    fun graal(version: String = GRAALVM_DEFAULT_SVM_VERSION) {
        dependencies += Library(
            COMPILE_ONLY_CONFIGURATION_NAME,
            GRAAL_DEPENDENCY_GROUP,
            GRAAL_DEPENDENCY_ARTIFACT,
            version,
        )
    }

    fun lombok(version: String) {
        dependencies += Library(
            COMPILE_ONLY_CONFIGURATION_NAME,
            LOMBOK_DEPENDENCY_GROUP,
            LOMBOK_DEPENDENCY_ARTIFACT,
            version
        )
        dependencies += Library(
            ANNOTATION_PROCESSOR_CONFIGURATION_NAME,
            LOMBOK_DEPENDENCY_GROUP,
            LOMBOK_DEPENDENCY_ARTIFACT,
            version
        )
    }

    data class Library(val configuration: String, val group: String, val name: String, val version: String)
}
