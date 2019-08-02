package ru.art.gradle.configurator.project

import org.eclipse.jgit.api.*
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode.*
import org.eclipse.jgit.transport.*
import org.gradle.api.*
import org.gradle.api.Project.*
import org.jetbrains.kotlin.backend.common.*
import ru.art.gradle.constants.*
import ru.art.gradle.constants.ConfigurationParameterMode.*
import ru.art.gradle.context.Context.git
import ru.art.gradle.context.Context.projectConfiguration
import ru.art.gradle.logging.*
import ru.art.gradle.logging.LogMessageColor.*

fun Project.setBranchByProperty() {
    if (hasProperty(USE_BRANCH) && projectConfiguration().projectVersionConfiguration.branchParameter.isNullOrBlank()) {
        (property(USE_BRANCH) as String).onlyIf({ isNotEmpty() }, projectConfiguration().projectVersionConfiguration::byBranchValue)
    }
}

fun Project.calculateVersion() {
    if (project.version.toString().isNotEmpty() && project.version != DEFAULT_VERSION) {
        success("Project version is '${project.version}'")
        return
    }

    if (hasProperty(USE_VERSION) && projectConfiguration().projectVersionConfiguration.versionProperty.isNullOrBlank()) {
        version = properties[USE_VERSION] as String
        success("Project version is '${project.version}'(manually set by 'useVersion' property)")
        return
    }

    if (!projectConfiguration().projectVersionConfiguration.versionProperty.isNullOrBlank()) {
        version = properties[projectConfiguration().projectVersionConfiguration.versionProperty] as String
        success("Project version is '${project.version}'(calculated from 'property=${projectConfiguration().projectVersionConfiguration.versionProperty}')")
        return
    }

    if (projectConfiguration().projectVersionConfiguration.versionByBranch) {
        val git = git()
        if (git == null) {
            warning("Unable to calculate project version, because git repository was not loaded correctly")
            return
        }
        if (projectConfiguration().projectVersionConfiguration.branchParameterMode == null) {
            version = git.repository.branch
            success("Project version is '${project.version}'(calculated from 'branch=${git.repository.branch}')")
            return
        }
        when (projectConfiguration().projectVersionConfiguration.branchParameterMode) {
            PROPERTY -> checkoutBranch(git, properties[projectConfiguration().projectVersionConfiguration.branchParameter] as String)
            VALUE -> checkoutBranch(git, projectConfiguration().projectVersionConfiguration.branchParameter!!)
        }
        version = git.repository.branch
        success("Project version is '${project.version}'(calculated from 'branch=${git.repository.branch}')")
        return
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