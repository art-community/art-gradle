package ru.adk.gradle.plugin.constants

enum class AdkMajorVersion(val majorTag: Int) {
    RELEASE_1(1),
    RELEASE_2(2),
    RELEASE_3(3);

    companion object {
        fun latest() = RELEASE_3
    }
}