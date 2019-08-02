package ru.art.gradle.provider

import com.github.lkishalmi.gradle.gatling.*
import org.gradle.api.*
import org.gradle.api.plugins.quality.*
import org.gradle.api.tasks.*
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
import ru.art.gradle.constants.DefaultTasks.GATLING_RUN
import ru.art.gradle.constants.DefaultTasks.JAR
import ru.art.gradle.constants.DefaultTasks.JMH_COMPILE_GENERATED_CLASSES
import ru.art.gradle.constants.DefaultTasks.TEST
import ru.art.gradle.constants.DefaultTasks.UPLOAD_ARCHIVES

fun Project.compileJavaTask() = tasks.getByPath(COMPILE_JAVA) as JavaCompile
fun Project.compileTestJavaTask() = tasks.getByPath(COMPILE_TEST_JAVA) as JavaCompile
fun Project.jarTask() = tasks.getByPath(JAR) as Jar
fun Project.buildTask() = tasks.getByPath(BUILD) as Task
fun Project.cleanTask() = tasks.getByPath(CLEAN) as Task
fun Project.testTask() = tasks.getByPath(TEST) as Test
fun Project.checkTask() = tasks.getByPath(CHECK) as DefaultTask
fun Project.checkstyleMainTask() = tasks.getByPath(CHECKSTYLE_MAIN) as Checkstyle
fun Project.checkstyleTestTask() = tasks.getByPath(CHECKSTYLE_TEST) as Checkstyle
fun Project.uploadTask() = tasks.getByPath(UPLOAD_ARCHIVES) as Upload
fun Project.jmhCompileGeneratedClassesTask() = tasks.getByPath(JMH_COMPILE_GENERATED_CLASSES) as Task
fun Project.gatlingRunTask() = tasks.getByPath(GATLING_RUN) as GatlingRunTask