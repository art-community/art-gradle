/*
 * ART Java
 *
 * Copyright 2019 ART
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

package ru.art.gradle.configurator.project

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
fun Project.applyJavaLibraryPlugin() = pluginManager.apply(JavaLibraryPlugin::class.java).run { success("JavaLibrary plugin applied") }
fun Project.applyCheckStylePlugin() = pluginManager.apply(CheckstylePlugin::class.java).run { success("Checkstyle plugin applied") }
fun Project.applyJmhPlugin() = pluginManager.apply(JMHPlugin::class.java).run { success("JMH plugin applied") }
fun Project.applyKotlinPlugin() = pluginManager.apply(KotlinPlatformJvmPlugin::class.java).run { success("Kotlin plugin applied") }
fun Project.applyScalaPlugin() = pluginManager.apply(ScalaPlugin::class.java).run { success("Scala plugin applied") }
fun Project.applyProtobufPlugin() = pluginManager.apply(ProtobufPlugin::class.java).run { success("Protobuf generator plugin applied") }