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

package ru.art.gradle.constants

import org.apache.tools.ant.taskdefs.condition.Os.*
import java.io.File.*

const val WEB_TASK_GROUP = "web"
const val PREPARE_WEB = "prepareWeb"
const val BUILD_WEB = "buildWeb"
const val CLEAN_WEB = "cleanWeb"
val WEB_SOURCE_SET = "src${separator}main${separator}web"
val WEB_SOURCE_SET_DIST = "src${separator}main${separator}web${separator}dist"
val NPM = if (isFamily(FAMILY_WINDOWS)) arrayOf("cmd", "/c", "npm") else arrayOf("npm")
val PREPARE_WEB_COMMAND = if (isFamily(FAMILY_WINDOWS)) listOf("cmd", "/c", "npm install") else listOf("npm", "install")
val BUILD_WEB_COMMAND = if (isFamily(FAMILY_WINDOWS)) listOf("cmd", "/c", "npm run production") else listOf("npm", "run", "production")