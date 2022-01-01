/*
 * ART
 *
 * Copyright 2019-2022 ART
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

package io.art.gradle.common.service

import io.art.gradle.common.constants.JAR_OPTION
import java.lang.Runtime.getRuntime
import java.nio.file.Path

data class JavaForkRequest(
        val executable: Path,
        val jar: Path,
        val arguments: List<String>,
        val directory: Path,
)

object ProcessExecutionService {
    fun forkProcess(command: Array<String>, directory: Path) {
        runCatching {
            getRuntime().exec(command, emptyArray(), directory.toFile())
        }
    }

    fun forkProcess(command: String, arguments: Array<String>, directory: Path) {
        forkProcess(listOf(command, *arguments).toTypedArray(), directory)
    }

    fun forkJava(request: JavaForkRequest) {
        val forkArguments = listOf(
                request.executable.toFile().absolutePath,
                *request.arguments.toTypedArray(),
                JAR_OPTION, request.jar.toFile().absolutePath,
        )
        forkProcess(forkArguments.toTypedArray(), request.directory)
    }
}
