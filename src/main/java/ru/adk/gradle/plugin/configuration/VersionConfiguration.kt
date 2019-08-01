package ru.adk.gradle.plugin.configuration

import ru.adk.gradle.plugin.constants.ConfigurationParameterMode
import ru.adk.gradle.plugin.constants.ConfigurationParameterMode.PROPERTY
import ru.adk.gradle.plugin.constants.ConfigurationParameterMode.VALUE

open class VersionConfiguration {
    var versionProperty: String? = null
        private set
    var branchParameter: String? = null
        private set
    var branchParameterMode: ConfigurationParameterMode? = null
        private set
    var versionByBranch = true

    fun byBranchValue(value: String) {
        branchParameter = value
        branchParameterMode = VALUE
        versionByBranch = true
    }

    fun byBranchProperty(property: String) {
        branchParameter = property
        branchParameterMode = PROPERTY
        versionByBranch = true
    }

    fun versionAsProperty(property: String) {
        versionProperty = property
        versionByBranch = false
    }
}