/*
 * ART Java
 *
 * Copyright 2019 ART
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.art.gradle.constants

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