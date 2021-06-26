/*
 * ART
 *
 * Copyright 2019-2021 ART
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

package io.art.gradle.common.logger

import io.art.gradle.common.constants.EMPTY_STRING
import io.art.gradle.common.constants.LOG_TEMPLATE
import io.art.gradle.common.constants.NEW_LINE
import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class ContextLogger(private val context: String, private val project: Project) {
    fun quiet(message: String) = project.quiet(message, context)
    fun success(message: String) = project.success(message, context)
    fun warning(message: String) = project.warning(message, context)
    fun attention(message: String) = project.attention(message, context)
    fun additional(message: String) = project.additional(message, context)
    fun error(message: String) = project.error(message, context)
    fun error(error: Throwable) = project.error(error, context)
    fun info(message: String) = project.info(message, context)
    fun debug(message: String) = project.debug(message, context)
    fun line() = project.quiet(EMPTY_STRING)

    fun output() = object : OutputStream() {
        val buffer = ByteArrayOutputStream()

        override fun write(byte: Int) = buffer.write(byte)

        override fun flush() = buffer.toString()
                .lineSequence()
                .filter { line -> line.isNotBlank() }
                .map { line -> LOG_TEMPLATE(context, line) }
                .joinToString(NEW_LINE)
                .let(project.logger::quiet)
    }

    fun error() = object : OutputStream() {
        val buffer = ByteArrayOutputStream()

        override fun write(byte: Int) = buffer.write(byte)

        override fun flush() = buffer.toString()
                .lineSequence()
                .filter { line -> line.isNotBlank() }
                .map { line -> LOG_TEMPLATE(context, line) }
                .joinToString(NEW_LINE)
                .let(project.logger::quiet)
    }
}
