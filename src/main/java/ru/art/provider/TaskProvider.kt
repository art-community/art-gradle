package ru.art.provider

import com.github.lkishalmi.gradle.gatling.GatlingRunTask
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.tasks.Upload
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.tasks.Jar
import ru.art.constants.DefaultTasks.BUILD
import ru.art.constants.DefaultTasks.CHECK
import ru.art.constants.DefaultTasks.CHECKSTYLE_MAIN
import ru.art.constants.DefaultTasks.CHECKSTYLE_TEST
import ru.art.constants.DefaultTasks.CLEAN
import ru.art.constants.DefaultTasks.COMPILE_JAVA
import ru.art.constants.DefaultTasks.COMPILE_TEST_JAVA
import ru.art.constants.DefaultTasks.GATLING_RUN
import ru.art.constants.DefaultTasks.JAR
import ru.art.constants.DefaultTasks.JMH_COMPILE_GENERATED_CLASSES
import ru.art.constants.DefaultTasks.TEST
import ru.art.constants.DefaultTasks.UPLOAD_ARCHIVES

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