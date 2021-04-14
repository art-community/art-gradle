package io.art.gradle.internal.logger

import io.art.gradle.internal.constants.LOG_TEMPLATE
import io.art.gradle.internal.constants.NEW_LINE
import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class ContextualLogger(private val context: String, private val project: Project) {
    fun quiet(message: String) = project.quiet(message, context)
    fun success(message: String) = project.success(message, context)
    fun warning(message: String) = project.warning(message, context)
    fun attention(message: String) = project.attention(message, context)
    fun additional(message: String) = project.additional(message, context)
    fun error(message: String) = project.error(message, context)
    fun error(error: Throwable) = project.error(error, context)
    fun info(message: String) = project.info(message, context)
    fun debug(message: String) = project.debug(message, context)

    fun output() = object : OutputStream() {
        val buffer = ByteArrayOutputStream()

        override fun write(byte: Int) = buffer.write(byte)

        override fun flush() = buffer.toString()
                .lineSequence()
                .filter { line -> line.isNotBlank() }
                .map { line -> LOG_TEMPLATE(context, line) }
                .joinToString(NEW_LINE)
                .let { line -> project.logger.quiet(message(line, LogMessageColor.CYAN_BOLD)) }
    }

    fun error() = object : OutputStream() {
        val buffer = ByteArrayOutputStream()

        override fun write(byte: Int) = buffer.write(byte)

        override fun flush() = buffer.toString()
                .lineSequence()
                .filter { line -> line.isNotBlank() }
                .map { line -> LOG_TEMPLATE(context, line) }
                .joinToString(NEW_LINE)
                .let { line -> project.logger.quiet(message(line, LogMessageColor.RED_BOLD)) }
    }
}
