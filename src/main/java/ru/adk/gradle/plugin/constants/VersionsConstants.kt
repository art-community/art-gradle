package ru.adk.gradle.plugin.constants

const val RELEASE_TAG_PREFIX = "release"
const val RELEASE_BRANCH = "release"
const val RELEASE_CANDIDATE_BRANCH = "release-candidate"
const val DEVELOPMENT_BRANCH = "development"
const val VERSIONING_TAG = "versioning"
const val VERSIONS_TAG = "versions"
const val VERSION_TAG = "version"
const val MAVEN_METADATA_XML = "maven-metadata.xml"
val TAG_PATTERN = { prefix: String -> Regex("$prefix-(\\d+\\.\\d+)") }
val MAJOR_RELEASE_PATTERN = { major: String -> Regex("release-($major\\.\\d+)") }