package ru.adk.gradle.plugin.constants

import java.io.File.*

val PROTO_DIRECTORY = "src${separator}generated${separator}proto"
val PROTOBUF_COMPILER_ARTIFACT = { version: String -> "com.google.protobuf:protoc:$version" }