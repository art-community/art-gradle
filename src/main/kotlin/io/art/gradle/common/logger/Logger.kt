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

import io.art.gradle.common.constants.LOG_TEMPLATE
import org.gradle.api.Project

fun Project.logger(context: String) = ContextLogger(context, this)

fun Project.log(message: String, context: String = project.name) {
    logger.quiet(LOG_TEMPLATE(context, message))
}

fun Project.error(message: String, context: String = project.name) {
    logger.error(LOG_TEMPLATE(context, message))
}

fun Project.error(throwable: Throwable, context: String = project.name) {
    logger.error(LOG_TEMPLATE(context, throwable.message ?: throwable.localizedMessage), throwable)
}

fun Project.info(message: String, context: String = project.name) {
    logger.info(LOG_TEMPLATE(context, message))
}

fun Project.debug(message: String, context: String = project.name) {
    logger.debug(LOG_TEMPLATE(context, message))
}
