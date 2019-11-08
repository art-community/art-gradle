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

package ru.art.gradle.configuration

import ru.art.gradle.constants.DefaultDependencyVersions.APACHE_HTTP_ASYNC_CLIENT_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.APACHE_HTTP_CLIENT_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.APACHE_HTTP_CORE_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.CGLIB_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.CHECKSTYLE_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.COMMONS_LOGGING_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.DROPWIZARD_METRICS_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.DROPWIZARD_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.EASY_RANDOM_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.GATLING_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.GROOVY_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.GRPC_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.GUAVA_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.HIKARI_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.JACKSON_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.JAVA_POET_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.JOOQ_PLUGIN_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.JOOQ_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.JUNIT_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.KAFKA_LOG4J_APPENDER_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.KAFKA_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.KOTLIN_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.LOG4J_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.LOGBACK_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.LOGBOOK_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.LOMBOK_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.MEMBRANE_SOA_SERVICE_PROXY_CORE_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.MESSAGE_PACK_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.METRICS_DROPWIZ_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.MICROMETER_JVM_EXTRAS_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.MICROMETER_PROMETHEUS_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.NETTY_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.PEBBLE_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.PROJECT_REACTOR_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.PROMETHEUS_DROPWIZARD_SIMPLE_CLIENT
import ru.art.gradle.constants.DefaultDependencyVersions.PROTOBUF_COMPILER_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.PROTOBUF_GRADLE_PLUGIN_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.RESILIENCE4J_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.ROCKSDB_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.RSOCKET_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.SCALA_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.SL4J_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.SPOCK_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.STAX_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.TARANTOOL_CONNECTOR_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.TOMCAT_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.TYPESAFE_CONFIG_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.VAVR_VERSION
import ru.art.gradle.constants.DefaultDependencyVersions.ZERO_TURNAROUND_VERSION

open class ExternalDependencyVersionsConfiguration {
    var cglibVersion = CGLIB_VERSION
    var jacksonVersion = JACKSON_VERSION
    var protobufGradlePluginVersion = PROTOBUF_GRADLE_PLUGIN_VERSION
    var protobufCompilerVersion = PROTOBUF_COMPILER_VERSION
    var checkstyleVersion = CHECKSTYLE_VERSION
    var typesafeConfigVersion = TYPESAFE_CONFIG_VERSION
    var resilience4jVersion = RESILIENCE4J_VERSION
    var metricsDropwizVersion = METRICS_DROPWIZ_VERSION
    var grpcVersion = GRPC_VERSION
    var apacheHttpAsyncClientVersion = APACHE_HTTP_ASYNC_CLIENT_VERSION
    var apacheHttpClientVersion = APACHE_HTTP_CLIENT_VERSION
    var apacheHttpCoreVersion = APACHE_HTTP_CORE_VERSION
    var logbookVersion = LOGBOOK_VERSION
    var logbackVersion = LOGBACK_VERSION
    var log4jVersion = LOG4J_VERSION
    var tomcatVersion = TOMCAT_VERSION
    var groovyVersion = GROOVY_VERSION
    var jooqVersion = JOOQ_VERSION
    var staxVersion = STAX_VERSION
    var lombokVersion = LOMBOK_VERSION
    var rocksdbVersion = ROCKSDB_VERSION
    var spockVersion = SPOCK_VERSION
    var micrometerPrometheusVersion = MICROMETER_PROMETHEUS_VERSION
    var micrometerJvmExtrasVersion = MICROMETER_JVM_EXTRAS_VERSION
    var prometheusDropwizardSimpleClientVersion = PROMETHEUS_DROPWIZARD_SIMPLE_CLIENT
    var dropwizardMetricsVersion = DROPWIZARD_METRICS_VERSION
    var hikariVersion = HIKARI_VERSION
    var jooqPluginVersion = JOOQ_PLUGIN_VERSION
    var sl4jVersion = SL4J_VERSION
    var guavaVersion = GUAVA_VERSION
    var javaPoetVersion = JAVA_POET_VERSION
    var projectReactorVersion = PROJECT_REACTOR_VERSION
    var rsocketVersion = RSOCKET_VERSION
    var kotlinVersion = KOTLIN_VERSION
    var scalaVersion = SCALA_VERSION
    var gatlingVersion = GATLING_VERSION
    var kafkaVersion = KAFKA_VERSION
    var kafkaLog4jAppenderVersion = KAFKA_LOG4J_APPENDER_VERSION
    var junitVersion = JUNIT_VERSION
    var tarantoolConnectorVersion = TARANTOOL_CONNECTOR_VERSION
    var zeroTurnaroundVersion = ZERO_TURNAROUND_VERSION
    var messagePackVersion = MESSAGE_PACK_VERSION
    var membraneSoaServiceProxyCoreVersion = MEMBRANE_SOA_SERVICE_PROXY_CORE_VERSION
    var pebbleVersion = PEBBLE_VERSION
    var vavrVersion = VAVR_VERSION
    var nettyVersion = NETTY_VERSION
    var easyRandomVersion = EASY_RANDOM_VERSION
    var dropwizardVersion = DROPWIZARD_VERSION
    var commonsLoggingVersion = COMMONS_LOGGING_VERSION
}