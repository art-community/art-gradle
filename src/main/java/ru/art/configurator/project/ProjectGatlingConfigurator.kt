package ru.art.configurator.project

import com.github.lkishalmi.gradle.gatling.GatlingPluginExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.ScalaSourceSet
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withConvention
import ru.art.constants.*
import ru.art.constants.DependencyConfiguration.GATLING
import ru.art.constants.DependencyConfiguration.PROVIDED
import ru.art.logging.LogMessageColor.PURPLE_BOLD
import ru.art.logging.message
import ru.art.logging.success
import ru.art.provider.buildTask
import ru.art.provider.gatlingRunTask

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
