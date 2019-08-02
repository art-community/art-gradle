package ru.art.gradle.configurator.project

import com.github.lkishalmi.gradle.gatling.*
import org.gradle.api.*
import org.gradle.api.plugins.*
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.DependencyConfiguration.*
import ru.art.gradle.constants.DependencyConfiguration.GATLING
import ru.art.gradle.logging.*
import ru.art.gradle.logging.LogMessageColor.*
import ru.art.gradle.provider.*

fun Project.configureGatling() {
    configure<GatlingPluginExtension> {
        simulations = fileTree(SIMULATIONS_DIR).files.map { "$SIMULATIONS_PREFIX.${it.name.removeSuffix(SCALA_POSTFIX)}" }
    }

    convention.getPlugin(JavaPluginConvention::class.java).sourceSets {
        GATLING {
            withConvention(ScalaSourceSet::class) {
                scala { source ->
                    source.setSrcDirs(source.srcDirs.apply { add(file(GATLING_SOURCE_SET_DIR)) })
                }
            }
        }
    }

    addDependency(GATLING, logbackClassic())
    addDependency(GATLING, scala())
    addDependency(PROVIDED, scala())
    addDependency(PROVIDED, gatlingHttp())
    addDependency(PROVIDED, gatlingCore())

    gatlingRunTask().dependsOn(buildTask())

    success("Configuring Gatling:\n" + message("""
        Simulations = ${fileTree(SIMULATIONS_DIR).files.map { "$SIMULATIONS_PREFIX.${it.name.removeSuffix(SCALA_POSTFIX)}" }}
        Sources directory = $GATLING_SOURCE_SET_DIR
        (!) gatlingRun depends on build
        """.replaceIndent(ADDITIONAL_LOGGING_MESSAGE_INDENT), PURPLE_BOLD))
}
