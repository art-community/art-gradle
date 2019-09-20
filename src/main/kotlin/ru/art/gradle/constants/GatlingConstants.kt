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

package ru.art.gradle.constants

import java.io.File.*

val GATLING_SOURCE_SET_DIR = "src${separator}gatling"
val RUN_GATLING_SIMULATION_TASK = { simulation: String -> "run${simulation.capitalize().replace(DOT, EMPTY_STRING)}" }
val GATLING_REPORTS = "reports${separator}gatling"
const val GATLING_MAIN_CLASS = "io.gatling.app.Gatling"
const val SIMULATION_MARKER = "extends Simulation"
const val SCALA_EXTENSION = "scala"
const val CREATE_GATLING_LAUNCHER_TASK = "createGatlingLauncher"
const val GATLING_LAUNCHER = "gatlingLauncher"
const val RUN_ALL_GATLING_SIMULATIONS_TASK = "runAllGatlingSimulations"
const val GATLING_GROUP = "gatling"
val GATLING_JVM_ARGS = listOf("-server",
        "-Xmx1G",
        "-XX:+UseG1GC",
        "-XX:MaxGCPauseMillis=30",
        "-XX:G1HeapRegionSize=16m",
        "-XX:InitiatingHeapOccupancyPercent=75",
        "-XX:+ParallelRefProcEnabled",
        "-XX:+PerfDisableSharedMem",
        "-XX:+AggressiveOpts",
        "-XX:+OptimizeStringConcat",
        "-XX:+HeapDumpOnOutOfMemoryError",
        "-Djava.net.preferIPv4Stack=true",
        "-Djava.net.preferIPv6Addresses=false")