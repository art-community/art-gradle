package ru.art.gradle.constants

const val MODEL_PACKAGE = "model"
const val MAPPING_PACKAGE = "mapping"
const val SERVICE_PACKAGE = "service"
const val GENERATOR_GROUP = "generator"
const val BUILD_MODEL_TASK = "buildModel"
const val BUILD_SERVICE_TASK = "buildService"
const val GENERATE_MAPPERS_TASK = "generateMappers"
const val GENERATOR_HTTP_GROUP = "generator.http"
const val GENERATOR_GRPC_GROUP = "generator.grpc"
const val GENERATOR_RSOCKET_GROUP = "generator.rsocket"
const val GENERATOR_SOAP_GROUP = "generator.soap"

val GENERATE_HTTP_SPECIFICATION_TASK = { service: String -> "generateHttp${service}Specification" }
val GENERATE_HTTP_PROXY_SPECIFICATION_TASK = { service: String -> "generateHttp${service}ProxySpecification" }
val GENERATE_GRPC_SPECIFICATION_TASK = { service: String -> "generateGrpc${service}Specification" }
val GENERATE_RSOCKET_SPECIFICATION_TASK = { service: String -> "generateRsocket${service}Specification" }
val GENERATE_SOAP_SPECIFICATION_TASK = { service: String -> "generateSoap${service}Specification" }

enum class SpecificationType {
    HTTP,
    HTTP_PROXY,
    SOAP,
    GRPC,
    RSOCKET
}

enum class GeneratorType {
    MAPPING,
    SPECIFICATION
}