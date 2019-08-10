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

package ru.art.gradle.configurator.project

import org.eclipse.jgit.api.*
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode.*
import org.eclipse.jgit.transport.*
import org.gradle.api.*
import org.gradle.api.Project.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.ProjectVersionCalculationMode.*
import ru.art.gradle.context.Context.git
import ru.art.gradle.context.Context.projectExtension
import ru.art.gradle.logging.*
import ru.art.gradle.logging.LogMessageColor.*

fun Project.calculateVersion() {
    if (project.version.toString().isNotEmpty() && project.version != DEFAULT_VERSION) {
        success("Project version is '${project.version}'")
        return
    }

    if (hasProperty(PROJECT_VERSION)) {
        version = properties[PROJECT_VERSION] as String
        success("Project version is '${project.version}'(manually set by 'projectVersion' property)")
        return
    }

    when (projectExtension().projectVersionConfiguration.calculationMode) {
        ROOT_PROJECT -> {
            version = rootProject.version
            success("Project version is '${project.version}'(calculated from 'rootProject=${rootProject.name}')")
        }
        BRANCH -> {
            val git = git()
            if (git == null) {
                warning("Unable to calculate project version, because git repository was not loaded correctly")
                return
            }
            if (hasProperty(CHECKOUT_BRANCH)) {
                checkoutBranch(git, properties[CHECKOUT_BRANCH] as String)
            }
            version = git.repository.branch
            success("Project version is '${project.version}'(calculated from 'branch=${git.repository.branch}')")
        }
    }
}

private fun Project.checkoutBranch(git: Git, branch: String) {
    try {
        git.checkout().setName(branch).setForceRefUpdate(true).call()
    } catch (e: Exception) {
        git.branchCreate()
                .setName(branch)
                .setUpstreamMode(SET_UPSTREAM)
                .setStartPoint("$ORIGIN_REF/$branch")
                .setForce(true)
                .call()
        git.checkout().setName(branch).setForceRefUpdate(true).call()
    }
    with(git.pull()) {
        if (hasProperty(GIT_USERNAME_PROPERTY) && hasProperty(GIT_PASSWORD_PROPERTY)) {
            setCredentialsProvider(UsernamePasswordCredentialsProvider(property(GIT_USERNAME_PROPERTY) as String, property(GIT_PASSWORD_PROPERTY) as String))
        }
        call()
    }
    additionalAttention("Checkout branch: '$branch'", PURPLE_BOLD)
}