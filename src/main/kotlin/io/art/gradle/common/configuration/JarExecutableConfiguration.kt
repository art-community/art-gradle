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

package io.art.gradle.common.configuration

import io.art.gradle.common.constants.DEFAULT_JAR_EXCLUSIONS
import org.gradle.api.JavaVersion.VERSION_1_9
import org.gradle.api.JavaVersion.current
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.DuplicatesStrategy.EXCLUDE
import org.gradle.api.tasks.JavaExec
import org.gradle.jvm.tasks.Jar
import javax.inject.Inject

open class JarExecutableConfiguration @Inject constructor() {
    var duplicateStrategy: DuplicatesStrategy = EXCLUDE
        private set
    var multiRelease = current().isCompatibleWith(VERSION_1_9)
        private set
    var runConfigurator: JavaExec.() -> Unit = {}
        private set
    var buildConfigurator: Jar.() -> Unit = {}
        private set
    var asBuildDependency: Boolean = true
        private set

    var manifestAttributes = mutableMapOf<String, String>()
        private set

    var manifestAttributesReplacer: (current: Map<String, String>) -> Map<String, String> = { it }
        private set

    var exclusions = DEFAULT_JAR_EXCLUSIONS.toMutableSet()
        private set

    fun resolveDuplicates(strategy: DuplicatesStrategy) {
        duplicateStrategy = strategy
    }

    fun multiRelease(multiRelease: Boolean = true) {
        this.multiRelease = multiRelease
    }

    fun addManifestAttributes(attributes: Map<String, String>) {
        manifestAttributes.putAll(attributes)
    }

    fun addManifestAttribute(name: String, value: String) {
        manifestAttributes[name] = value
    }

    fun replaceManifestAttributes(attributes: (current: Map<String, String>) -> Map<String, String>) {
        manifestAttributesReplacer = attributes
    }

    fun addExclusions(vararg exclusions: String) {
        this.exclusions.addAll(exclusions)
    }

    fun replaceExclusions(exclusions: (current: Set<String>) -> Set<String>) {
        this.exclusions = exclusions(this.exclusions).toMutableSet()
    }

    fun buildDependsOn(buildDependsOn: Boolean = true) {
        asBuildDependency = buildDependsOn
    }

    fun configureRun(runConfigurator: JavaExec.() -> Unit) {
        this.runConfigurator = runConfigurator
    }

    fun configureBuild(buildConfigurator: Jar.() -> Unit) {
        this.buildConfigurator = buildConfigurator
    }
}
