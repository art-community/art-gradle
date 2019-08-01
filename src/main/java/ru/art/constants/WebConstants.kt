package ru.art.constants

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