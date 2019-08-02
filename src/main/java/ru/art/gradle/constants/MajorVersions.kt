package ru.art.gradle.constants

enum class ARTMajorVersion(val majorTag: Int) {
    RELEASE_1(1);

    companion object {
        fun latest() = RELEASE_1
    }
}