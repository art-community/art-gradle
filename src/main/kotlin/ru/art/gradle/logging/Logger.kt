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

package ru.art.gradle.logging

import org.gradle.api.*
import ru.art.gradle.constants.*
import ru.art.gradle.context.Context.auxiliaryInformation
import ru.art.gradle.logging.LogMessageColor.*

const val ANSI_RESET = "\u001B[0m"

enum class LogMessageColor(val code: String) {
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m"),
    BLACK_BOLD("\u001b[1;30m"),
    RED_BOLD("\u001b[1;31m"),
    GREEN_BOLD("\u001b[1;32m"),
    YELLOW_BOLD("\u001b[1;33m"),
    BLUE_BOLD("\u001b[1;34m"),
    PURPLE_BOLD("\u001b[1;35m"),
    CYAN_BOLD("\u001b[1;36m"),
    WHITE_BOLD("\u001b[1;37m")
}

fun message(message: String, color: LogMessageColor) = "${color.code}$message$ANSI_RESET"

fun Project.quiet(message: String, color: LogMessageColor = BLACK) {
    logger.quiet("${name(color)} ${color.code}$message$ANSI_RESET")
}

fun Project.success(message: String, color: LogMessageColor = GREEN_BOLD) {
    logger.quiet("${name(color)} ${color.code}$message$ANSI_RESET")
}

fun Project.warning(message: String, color: LogMessageColor = YELLOW_BOLD) {
    logger.quiet("${name(color)} ${color.code}$message$ANSI_RESET")
}

fun Project.attention(message: String, color: LogMessageColor = CYAN_BOLD) {
    logger.quiet("${name(color)} ${color.code}$message$ANSI_RESET")
}

fun Project.additionalAttention(message: String, color: LogMessageColor = PURPLE_BOLD) {
    logger.quiet("${name(color)} ${color.code}$message$ANSI_RESET")
}

fun Project.error(message: String, color: LogMessageColor = RED) {
    logger.error("${name(color)} ${color.code}$message$ANSI_RESET")
}

fun Project.info(message: String, color: LogMessageColor = BLACK) {
    logger.info("${name(color)} ${color.code}$message$ANSI_RESET")
}

fun Project.debug(message: String, color: LogMessageColor = YELLOW) {
    logger.debug("${name(color)} ${color.code}$message$ANSI_RESET")
}

fun Project.logResultingConfiguration() {
    var message = "Project resulting configuration:\n"
    message += message("""
Name: $name
Group: $group
Version: $version
${buildAuxiliaryInformationMessage()}""".replaceIndent(ADDITIONAL_LOGGING_MESSAGE_INDENT), PURPLE_BOLD)
    attention(message)
}


private fun Project.buildAuxiliaryInformationMessage(): String {
    var message = ""
    if (auxiliaryInformation().hasGatling || auxiliaryInformation().hasJmh) {
        message += "Load testing frameworks:\n"
    }
    if (auxiliaryInformation().hasGatling) {
        message += message("Gatling\n", GREEN_BOLD)
    }
    if (auxiliaryInformation().hasJmh) {
        message += message("JMH\n", GREEN_BOLD)
    }

    message += message("Languages:\n", PURPLE_BOLD)
    message += message("\tJava\n", GREEN_BOLD)
    if (auxiliaryInformation().hasGroovy) {
        message += message("\tGroovy\n", GREEN_BOLD)
    }
    if (auxiliaryInformation().hasGroovyTests) {
        message += message("\tGroovy for tests\n", GREEN_BOLD)
    }
    if (auxiliaryInformation().hasKotlin) {
        message += message("\tKotlin\n", GREEN_BOLD)
    }
    if (auxiliaryInformation().hasKotlinTests) {
        message += message("\tKotlin for tests\n", GREEN_BOLD)
    }
    if (auxiliaryInformation().hasScala) {
        message += message("\tScala\n", GREEN_BOLD)
    }
    if (auxiliaryInformation().hasScalaTests) {
        message += message("\tScala for tests\n", GREEN_BOLD)
    }
    if (auxiliaryInformation().hasWeb) {
        message += message("\tWeb\n", GREEN_BOLD)
    }

    if (auxiliaryInformation().hasSpock) {
        message += message("Testing frameworks:\n", PURPLE_BOLD)
    }
    if (auxiliaryInformation().hasSpock) {
        message += message("\tSpockFramework\n", GREEN_BOLD)
    }

    if (auxiliaryInformation().hasCheckstyle) {
        message += message("Code checkers:\n", PURPLE_BOLD)
    }
    if (auxiliaryInformation().hasCheckstyle) {
        message += message("\tCheckstyle\n", GREEN_BOLD)
    }

    if (auxiliaryInformation().hasLombok) {
        message += message("Annotation processors:\n", PURPLE_BOLD)
    }
    if (auxiliaryInformation().hasLombok) {
        message += message("\tLombok\n", GREEN_BOLD)
    }

    if (auxiliaryInformation().hasGenerator || auxiliaryInformation().hasProtobufGenerator) {
        message += message("Generators:\n", PURPLE_BOLD)
    }
    if (auxiliaryInformation().hasGenerator) {
        message += message("\tART Generator\n", GREEN_BOLD)
    }
    if (auxiliaryInformation().hasProtobufGenerator) {
        message += message("\tProtobuf Generator\n", GREEN_BOLD)
    }
    return message.removeSuffix(NEW_LINE)
}

private fun Project.name(color: LogMessageColor) = message("[$name]:", color)