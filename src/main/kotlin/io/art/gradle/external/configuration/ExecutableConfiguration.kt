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

import io.art.gradle.external.constants.EXECUTABLE
import io.art.gradle.external.constants.MANIFEST_EXCLUSIONS
import io.art.gradle.external.plugin.externalPlugin
import org.gradle.api.Action
import org.gradle.api.JavaVersion.VERSION_1_9
import org.gradle.api.JavaVersion.current
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.DuplicatesStrategy.EXCLUDE
import org.gradle.api.model.ObjectFactory
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.newInstance
import org.gradle.process.JavaExecSpec
import java.nio.file.Path
import javax.inject.Inject

open class ExecutableConfiguration @Inject constructor(objectFactory: ObjectFactory) {
    var mainClass: String? = null
        private set
    var name: String = externalPlugin.project.name
        private set
    var directory: Path = externalPlugin.project.buildDir.resolve(EXECUTABLE).toPath()
        private set

    val jar = objectFactory.newInstance<JarExecutableConfiguration>()

    val native = objectFactory.newInstance<NativeExecutableConfiguration>()


    var jarEnabled = false
        private set

    var nativeEnabled = false
        private set


    fun name(name: String) {
        this.name = name
    }

    fun directory(directory: Path) {
        this.directory = directory
    }

    fun jar(action: Action<in JarExecutableConfiguration>) {
        action.execute(jar)
        jarEnabled = true
    }

    fun native(action: Action<in NativeExecutableConfiguration>) {
        action.execute(native)
        nativeEnabled = true
    }

    fun main(mainClass: String) {
        this.mainClass = mainClass
    }

    open class JarExecutableConfiguration @Inject constructor() {
        var classedDuplicateStrategy: DuplicatesStrategy = EXCLUDE
            private set
        var mainClass: String? = null
            private set
        var multiRelease = current().isCompatibleWith(VERSION_1_9)
            private set
        var runConfigurator: JavaExecSpec.() -> Unit = {}
            private set
        var buildConfigurator: Jar.() -> Unit = {}
            private set

        val manifestAdditionalAttributes = mutableMapOf<String, String>()

        val exclusions = MANIFEST_EXCLUSIONS.toMutableSet()


        fun resolveDuplicateClasses(strategy: DuplicatesStrategy) {
            classedDuplicateStrategy = strategy
        }

        fun multiRelease(multiRelease: Boolean = true) {
            this.multiRelease = multiRelease
        }

        fun manifestAttributes(attributes: Map<String, String>) {
            manifestAdditionalAttributes += attributes
        }

        fun exclusions(exclusions: Set<String>) {
            this.exclusions += exclusions
        }

        fun configureRun(runConfigurator: JavaExecSpec.() -> Unit) {
            this.runConfigurator = runConfigurator
        }

        fun configureBuild(buildConfigurator: Jar.() -> Unit) {
            this.buildConfigurator = buildConfigurator
        }
    }

    open class NativeExecutableConfiguration {
    }
}
