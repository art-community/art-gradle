package ru.adk.gradle.plugin.configuration

import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultJavaConfiguration.COMPILER_ENCODING
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultJavaConfiguration.SOURCE_COMPATIBILITY
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultJavaConfiguration.TARGET_COMPATIBILITY

open class JavaConfiguration {
    var compilerEncoding = COMPILER_ENCODING
    var sourceCompatibility = SOURCE_COMPATIBILITY
    var targetCompatibility = TARGET_COMPATIBILITY
}