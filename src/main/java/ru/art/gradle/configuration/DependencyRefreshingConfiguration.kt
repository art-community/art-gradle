package ru.art.gradle.configuration

import ru.art.gradle.constants.configuration.defaults.*

open class DependencyRefreshingConfiguration {
    var refreshingRateInSeconds = REFRESHING_RATE_IN_SECONDS
    private set

    fun refreshingRateInSecconds(value: Int) {
        refreshingRateInSeconds = value
    }
}