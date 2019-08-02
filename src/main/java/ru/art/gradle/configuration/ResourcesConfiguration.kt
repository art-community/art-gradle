package ru.art.gradle.configuration

import ru.art.gradle.constants.configuration.defaults.DefaultResourcesConfiguration.RESOURCES_DIR
import ru.art.gradle.constants.configuration.defaults.DefaultResourcesConfiguration.TEST_RESOURCES_DIR

open class ResourcesConfiguration {
    var resourceDirs = mutableListOf(RESOURCES_DIR)
    var testResourceDirs = mutableListOf(TEST_RESOURCES_DIR)
}