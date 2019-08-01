package ru.adk.gradle.plugin.configuration

import ru.adk.gradle.plugin.constants.configuration.defaults.REFRESHING_RATE_IN_SECONDS

open class DependencyRefreshingConfiguration {
    var refreshingRateInSeconds = REFRESHING_RATE_IN_SECONDS
    private set

    fun refreshingRateInSecconds(value: Int) {
        refreshingRateInSeconds = value
    }
}