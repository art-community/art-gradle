package ru.art.gradle.configuration

import org.gradle.api.*
import ru.art.gradle.constants.*
import javax.inject.*

open class GeneratorConfiguration @Inject constructor(val project: Project) {
    var group = ART_MODULE_GROUP
    var version = EMPTY_STRING
    var packageName = EMPTY_STRING
}