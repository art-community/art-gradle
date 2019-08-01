package ru.adk.gradle.plugin.git

import org.eclipse.jgit.api.*
import org.eclipse.jgit.api.Git.*
import org.gradle.api.*
import ru.adk.gradle.plugin.constants.*
import ru.adk.gradle.plugin.logging.*
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
            val git = open(File("${parent!!.projectDir.absolutePath}$separator${gitFile.readLines().first().split(SEMICOLON)[1].substringAfter(PREVIOUS_DIRECTORY).removeSuffix(NEW_LINE)}"))
            success("Git repository loaded")
            return git
        }
        warning("Git repository was not found")
        return null
    }
}