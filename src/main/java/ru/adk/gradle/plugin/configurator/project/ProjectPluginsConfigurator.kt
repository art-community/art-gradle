package ru.adk.gradle.plugin.configurator.project

import com.github.lkishalmi.gradle.gatling.GatlingPlugin
import com.google.protobuf.gradle.ProtobufPlugin
import me.champeau.gradle.JMHPlugin
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.plugins.quality.CheckstylePlugin
import org.gradle.api.plugins.scala.ScalaPlugin
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import ru.adk.gradle.plugin.logging.success

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