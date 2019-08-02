/*
 * Copyright 2019 ART
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

package ru.art.gradle.constants

object DefaultTasks {
    const val COMPILE_JAVA = "compileJava"
    const val COMPILE_TEST_JAVA = "compileTestJava"
    const val JAR = "jar"
    const val BUILD = "build"
    const val CLEAN = "clean"
    const val TEST = "test"
    const val CHECK = "check"
    const val CHECKSTYLE_MAIN = "checkstyleMain"
    const val CHECKSTYLE_TEST = "checkstyleTest"
    const val UPLOAD_ARCHIVES = "uploadArchives"
    const val UPLOAD_REPORTS = "uploadReports"
    const val JMH_COMPILE_GENERATED_CLASSES = "jmhCompileGeneratedClasses"
    const val GATLING_RUN = "gatlingRun"
}