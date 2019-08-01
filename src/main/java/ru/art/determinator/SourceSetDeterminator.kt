package ru.art.determinator

import org.gradle.api.*
import ru.art.constants.*
import java.io.File.*

fun Project.determineSourceSets(): SourceSetDeterminationResult {
    var hasGroovy = false
    var hasScala = false
    var hasKotlin = false
    var hasGatling = false
    var hasJmh = false
    var hasWeb = false
    var hasGroovyTests = false
    var hasScalaTests = false
    var hasKotlinTests = false
    val testSourceSet = file("$projectDir/$SRC/$TEST_SOURCE_SET")
    if (testSourceSet.exists()) {
        testSourceSet.listFiles()?.forEach { file ->
            hasGroovyTests = hasGroovyTests or file.name.contains(GROOVY)
            hasScalaTests = hasScalaTests or file.name.contains(SCALA)
            hasKotlinTests = hasKotlinTests or file.name.contains(KOTLIN)
        }
    }
    file("$projectDir$separator$SRC").walkTopDown()
            .maxDepth(2)
            .filter { !it.absolutePath.contains("$projectDir/$SRC/$TEST_SOURCE_SET") }
            .forEach { file ->
                hasGroovy = hasGroovy or file.name.contains(GROOVY)
                hasGatling = hasGatling or file.name.contains(GATLING)
                hasScala = hasScala or file.name.contains(SCALA)
                hasJmh = hasJmh or file.name.contains(JMH)
                hasKotlin = hasKotlin or file.name.contains(KOTLIN)
                hasWeb = hasWeb or file.name.contains(WEB)
            }
    return SourceSetDeterminationResult(
            hasGroovy = hasGroovy,
            hasScala = hasScala,
            hasKotlin = hasKotlin,
            hasGatling = hasGatling,
            hasJmh = hasJmh,
            hasWeb = hasWeb,
            hasGroovyTests = hasGroovyTests,
            hasScalaTests = hasScalaTests,
            hasKotlinTests = hasKotlinTests)
}

data class SourceSetDeterminationResult(val hasGroovy: Boolean,
                                        val hasScala: Boolean,
                                        val hasKotlin: Boolean,
                                        val hasGatling: Boolean,
                                        val hasJmh: Boolean,
                                        val hasWeb: Boolean,
                                        val hasGroovyTests: Boolean,
                                        val hasScalaTests: Boolean,
                                        val hasKotlinTests: Boolean)