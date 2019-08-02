package ru.art.gradle.configurator.project

import com.github.lkishalmi.gradle.gatling.*
import com.google.protobuf.gradle.*
import me.champeau.gradle.*
import org.gradle.api.*
import org.gradle.api.plugins.*
import org.gradle.api.plugins.quality.*
import org.gradle.api.plugins.scala.*
import org.gradle.plugins.ide.idea.*
import org.jetbrains.kotlin.gradle.plugin.*
import ru.art.gradle.logging.*

fun Project.applyJavaPlugin() = pluginManager.apply(JavaPlugin::class.java).run { success("Java plugin applied") }
fun Project.applyIdeaPlugin() = pluginManager.apply(IdeaPlugin::class.java).run { success("Idea plugin applied") }
fun Project.applyGroovyPlugin() = pluginManager.apply(GroovyPlugin::class.java).run { success("Groovy plugin applied") }
fun Project.applyMavenPlugin() = pluginManager.apply(MavenPlugin::class.java).run { success("Maven plugin applied") }
fun Project.applyJavaLibraryPlugin() = pluginManager.apply(JavaLibraryPlugin::class.java).run { success("JavaLibrary plugin applied") }
fun Project.applyCheckStylePlugin() = pluginManager.apply(CheckstylePlugin::class.java).run { success("Checkstyle plugin applied") }
fun Project.applyJmhPlugin() = pluginManager.apply(JMHPlugin::class.java).run { success("JMH plugin applied") }
fun Project.applyGatlingPlugin() = pluginManager.apply(GatlingPlugin::class.java).run { success("Gatling plugin applied") }
fun Project.applyKotlinPlugin() = pluginManager.apply(KotlinPlatformJvmPlugin::class.java).run { success("Kotlin plugin applied") }
fun Project.applyScalaPlugin() = pluginManager.apply(ScalaPlugin::class.java).run { success("Scala plugin applied") }
fun Project.applyProtobufPlugin() = pluginManager.apply(ProtobufPlugin::class.java).run { success("Protobuf generator plugin applied") }