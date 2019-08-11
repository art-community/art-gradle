/*
 *    Copyright 2019 ART 
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

package ru.art.gradle.constants

const val MODEL_PACKAGE = "model"
const val MAPPING_PACKAGE = "mapping"
const val SERVICE_PACKAGE = "service"
const val GENERATOR_GROUP = "generator"
const val GENERATE_MAPPERS_TASK = "generateValueMappersByModels"
const val COMPILE_MODELS_TASK = "compileModels"
const val COMPILE_SERVICES_TASK = "compileService"
const val GENERATOR_HTTP_GROUP = "generator.http"
const val GENERATOR_GRPC_GROUP = "generator.grpc"
const val GENERATOR_RSOCKET_GROUP = "generator.rsocket"
const val GENERATOR_SOAP_GROUP = "generator.soap"
const val JAVA_FILE_EXTENSION = ".java"
val DEFAULT_COMPILE_MODELS_SOURCES = mutableSetOf("model", "constants")
val DEFAULT_COMPILE_SERVICES_SOURCES = mutableSetOf("service", "mapping", "model", "dao", "constants")

val GENERATE_HTTP_SPECIFICATION_TASK = { service: String -> "generateHttp${service}Specification" }
val GENERATE_HTTP_PROXY_SPECIFICATION_TASK = { service: String -> "generateHttp${service}CommunicationSpecification" }
val GENERATE_GRPC_SPECIFICATION_TASK = { service: String -> "generateGrpc${service}Specification" }
val GENERATE_RSOCKET_SPECIFICATION_TASK = { service: String -> "generateRsocket${service}Specification" }
val GENERATE_SOAP_SPECIFICATION_TASK = { service: String -> "generateSoap${service}Specification" }

enum class SpecificationType {
    HTTP,
    HTTP_COMMUNICATION,
    SOAP,
    GRPC,
    RSOCKET
}

enum class GeneratorType {
    MAPPING,
    SPECIFICATION
}