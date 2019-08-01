package ru.art.configurator.project

import com.google.protobuf.gradle.*
import org.gradle.api.*
import org.gradle.kotlin.dsl.*
import ru.art.constants.*
import ru.art.context.Context.projectConfiguration
import java.io.File.*

fun Project.configureProtobufGenerator() {
    protobuf {
        protoc(closureOf<ExecutableLocator> {
            generatedFilesBaseDir = "${projectDir.absolutePath}$separator$PROTO_DIRECTORY"
            artifact = PROTOBUF_COMPILER_ARTIFACT(projectConfiguration().externalDependencyVersionsConfiguration.protobufCompilatorVersion)
        })
    }
}