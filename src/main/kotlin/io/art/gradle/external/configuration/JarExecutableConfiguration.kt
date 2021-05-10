package io.art.gradle.external.configuration

import io.art.gradle.external.constants.MANIFEST_EXCLUSIONS
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

    var manifestAdditionalAttributes = mutableMapOf<String, String>()
        private set

    var exclusions = MANIFEST_EXCLUSIONS.toMutableSet()
        private set


    fun resolveDuplicates(strategy: DuplicatesStrategy) {
        duplicateStrategy = strategy
    }

    fun multiRelease(multiRelease: Boolean = true) {
        this.multiRelease = multiRelease
    }

    fun addManifestAttributes(attributes: Map<String, String>) {
        manifestAdditionalAttributes.putAll(attributes)
    }

    fun addManifestAttribute(name: String, value: String) {
        manifestAdditionalAttributes[name] = value
    }

    fun replaceManifestAttributes(attributes: (current: Map<String, String>) -> Map<String, String>) {
        manifestAdditionalAttributes = attributes(manifestAdditionalAttributes).toMutableMap()
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
