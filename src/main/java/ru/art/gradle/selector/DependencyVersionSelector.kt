/*
 * Copyright 2019 ART
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.art.gradle.selector

import org.gradle.api.*
import org.gradle.api.artifacts.*
import ru.art.gradle.constants.*
import ru.art.gradle.logging.*

fun DependencyResolveDetails.selectVersionByProjectVersionsTree(version: String?, project: Project): String? {
    version ?: return EMPTY_STRING
    project.success("For dependency(${this.requested.group}:${this.requested.name}) select version '$version' by branch")
    return version
}

fun DependencyResolveDetails.selectVersionByArtMajorMinor(majorMinorVersion: ArtMajorMinorVersion?, project: Project): String? {
    majorMinorVersion ?: return EMPTY_STRING
    project.success("For dependency(${this.requested.group}${this.requested.name}) select latest ART major version: '${majorMinorVersion.version}'")
    return majorMinorVersion.version
}