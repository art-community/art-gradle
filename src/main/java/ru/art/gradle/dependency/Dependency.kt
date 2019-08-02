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

package ru.art.gradle.dependency

data class Dependency(val group: String,
                      val artifact: String,
                      val version: String? = null,
                      var exclusions: Set<Dependency> = setOf()) {
    fun inGradleNotation(): String {
        version ?: return "$group:$artifact"
        return if (version.isEmpty()) "$group:$artifact" else "$group:$artifact:$version"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Dependency

        if (group != other.group) return false
        if (artifact != other.artifact) return false

        return true
    }

    override fun hashCode(): Int {
        var result = group.hashCode()
        result = 31 * result + artifact.hashCode()
        return result
    }


}