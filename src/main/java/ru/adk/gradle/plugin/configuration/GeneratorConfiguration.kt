package ru.adk.gradle.plugin.configuration

import org.gradle.api.*
import ru.adk.gradle.plugin.constants.*
import javax.inject.*

open class GeneratorConfiguration @Inject constructor(val project: Project) {
    var group = ADK_MODULE_GROUP
    var version = EMPTY_STRING
    var packageName = EMPTY_STRING
}