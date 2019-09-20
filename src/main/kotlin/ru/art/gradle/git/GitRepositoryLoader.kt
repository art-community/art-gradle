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

package ru.art.gradle.git

import org.eclipse.jgit.api.*
import org.eclipse.jgit.api.Git.*
import org.gradle.api.*
import ru.art.gradle.constants.*
import ru.art.gradle.logging.*
import java.io.*
import java.io.File.*

fun Project.loadGitRepository(): Git? {
    try {
        val git = open(projectDir)
        success("Git repository loaded")
        return git
    } catch (e: Exception) {
        if (parent == null) {
            warning("Git repository loading was failed. Exception:$e")
            return null
        }
        val gitFile = file(GIT_FILE)
        if (gitFile.exists()) {
            val gitProjectPath = gitFile.readLines().first().split(COLON)[1].substringAfter(PREVIOUS_DIRECTORY).removeSuffix(NEW_LINE)
            val git = open(File("${parent!!.projectDir.absolutePath}$separator$gitProjectPath"))
            success("Git repository loaded")
            return git
        }
        warning("Git repository was not found")
        return null
    }
}