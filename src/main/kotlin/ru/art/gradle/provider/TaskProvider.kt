/*
 *    Copyright 2019 ART 
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.art.gradle.provider

import com.github.lkishalmi.gradle.gatling.*
import org.gradle.api.*
import org.gradle.api.plugins.quality.*
import org.gradle.api.tasks.compile.*
import org.gradle.api.tasks.testing.*
import org.gradle.jvm.tasks.*
import ru.art.gradle.constants.DefaultTasks.BUILD
import ru.art.gradle.constants.DefaultTasks.CHECK
import ru.art.gradle.constants.DefaultTasks.CHECKSTYLE_MAIN
import ru.art.gradle.constants.DefaultTasks.CHECKSTYLE_TEST
import ru.art.gradle.constants.DefaultTasks.CLEAN
import ru.art.gradle.constants.DefaultTasks.COMPILE_JAVA
import ru.art.gradle.constants.DefaultTasks.COMPILE_TEST_JAVA
import ru.art.gradle.constants.DefaultTasks.EXTRACT_INCLUDE_PROTO
import ru.art.gradle.constants.DefaultTasks.EXTRACT_PROTO
import ru.art.gradle.constants.DefaultTasks.GATLING_RUN
import ru.art.gradle.constants.DefaultTasks.GENERATE_PROTO
import ru.art.gradle.constants.DefaultTasks.JAR
import ru.art.gradle.constants.DefaultTasks.JMH_COMPILE_GENERATED_CLASSES
import ru.art.gradle.constants.DefaultTasks.TEST

fun Project.compileJavaTask() = tasks.getByPath(COMPILE_JAVA) as JavaCompile
fun Project.compileTestJavaTask() = tasks.getByPath(COMPILE_TEST_JAVA) as JavaCompile
fun Project.jarTask() = tasks.getByPath(JAR) as Jar
fun Project.buildTask() = tasks.getByPath(BUILD) as Task
fun Project.cleanTask() = tasks.getByPath(CLEAN) as Task
fun Project.testTask() = tasks.getByPath(TEST) as Test
fun Project.checkTask() = tasks.getByPath(CHECK) as DefaultTask
fun Project.checkstyleMainTask() = tasks.getByPath(CHECKSTYLE_MAIN) as Checkstyle
fun Project.checkstyleTestTask() = tasks.getByPath(CHECKSTYLE_TEST) as Checkstyle
fun Project.jmhCompileGeneratedClassesTask() = tasks.getByPath(JMH_COMPILE_GENERATED_CLASSES) as Task
fun Project.gatlingRunTask() = tasks.getByPath(GATLING_RUN) as GatlingRunTask
fun Project.extractIncludeProtoTask() = tasks.getByPath(EXTRACT_INCLUDE_PROTO) as DefaultTask
fun Project.extractProtoTask() = tasks.getByPath(EXTRACT_PROTO) as DefaultTask
fun Project.generateProtoTask() = tasks.getByPath(GENERATE_PROTO) as DefaultTask