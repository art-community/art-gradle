package ru.art.configuration

import ru.art.constants.configuration.defaults.DefaultJavaConfiguration.COMPILER_ENCODING
import ru.art.constants.configuration.defaults.DefaultJavaConfiguration.SOURCE_COMPATIBILITY
import ru.art.constants.configuration.defaults.DefaultJavaConfiguration.TARGET_COMPATIBILITY

open class JavaConfiguration {
    var compilerEncoding = COMPILER_ENCODING
    var sourceCompatibility = SOURCE_COMPATIBILITY
    var targetCompatibility = TARGET_COMPATIBILITY
}