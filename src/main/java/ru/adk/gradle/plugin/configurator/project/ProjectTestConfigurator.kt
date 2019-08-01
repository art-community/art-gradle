package ru.adk.gradle.plugin.configurator.project

import org.gradle.api.*
import org.gradle.api.tasks.bundling.*
import org.gradle.api.tasks.testing.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.backend.common.*
import org.jfrog.artifactory.client.ArtifactoryClientBuilder.*
import ru.adk.gradle.plugin.constants.*
import ru.adk.gradle.plugin.constants.DefaultTasks.UPLOAD_REPORTS
import ru.adk.gradle.plugin.constants.DependencyConfiguration.*
import ru.adk.gradle.plugin.constants.RepositoryType.*
import ru.adk.gradle.plugin.context.Context.projectConfiguration
import ru.adk.gradle.plugin.logging.*
import ru.adk.gradle.plugin.provider.*
import java.io.File.*

fun Project.configureTests() {
    addDependency(TEST_COMPILE_CLASSPATH, junit())

    with(testTask()) {
        useJUnit()
        success("Use JUnit in tests")

        beforeTest(closureOf<TestDescriptor> {
            logger.lifecycle(this.toString())
        })

        onOutput(KotlinClosure2<TestDescriptor, TestOutputEvent, Unit>({ descriptor, event ->
            logger.lifecycle("$descriptor - ${event.message}")
        }))
    }

    if (projectConfiguration().publishingConfiguration.getUsernameParameter(this@configureTests) == projectConfiguration().testConfiguration.reportsPublisher) {
        val uploadReportsTask = tasks.create(UPLOAD_REPORTS, Zip::class.java) { zipTask ->
            with(zipTask) {
                group = UPLOAD_REPORTS_GROUP
                archiveBaseName.set(REPORTS_ARCHIVE_NAME)
                from("$buildDir$separator$REPORTS_ARCHIVE_NAME")
                destinationDirectory.set(buildDir)
                doLast {
                    file("$buildDir$separator$REPORTS_ARCHIVE_NAME").onlyIf({ exists() && archiveFile.isPresent }, {
                        when (projectConfiguration().publishingConfiguration.repositoryType) {
                            NEXUS -> TODO()
                            ARTIFACTORY -> uploadReportsToArtifactory(this)
                        }
                    })
                }
            }
        }
        testTask().finalizedBy(uploadReportsTask)
    }

    buildTask().dependsOn.remove(checkTask())
    buildTask().dependsOn.remove(testTask())
    checkTask().dependsOn.remove(testTask())

    success("Finalize 'test' task by 'uploadReports' (send test reports to publishing repository)")
    additionalAttention("Disable 'check' task before 'build' task")
    additionalAttention("Disable 'test' task before 'build' task")
    additionalAttention("Disable 'check' task before 'test' task")
}

private fun Project.uploadReportsToArtifactory(zip: Zip) {
    val publishingUrl = projectConfiguration().publishingConfiguration.getUrlParameter(this)
    val publishingUsername = projectConfiguration().publishingConfiguration.getUsernameParameter(this)
    val publishingPassword = projectConfiguration().publishingConfiguration.getPasswordParameter(this)
    create()
            .run {
                url = publishingUrl
                username = publishingUsername
                password = publishingPassword
                build()
            }.run {
                repository(project.testArchiveRepositoryPath()).upload(zip.archiveFile.get().asFile.name, zip.archiveFile.get().asFile).doUpload()
                project.success("Uploading artifact '${zip.archiveFile.get().asFile.name}' to $publishingUrl/${project.testArchiveRepositoryPath()}")
                zip.archiveFile.get().asFile.delete()
            }
}

private fun Project.testArchiveRepositoryPath() = "/${projectConfiguration().publishingConfiguration.repositoryId}/${(group as String).replace(DOT, SLASH)}/$name/$version"