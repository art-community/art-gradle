package ru.art.gradle.configuration

import ru.art.gradle.constants.*
import ru.art.gradle.constants.ConfigurationParameterMode.*

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