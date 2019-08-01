package ru.adk.gradle.plugin.configuration

import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.APACHE_HTTP_ASYNC_CLIENT_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.APACHE_HTTP_CLIENT_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.APACHE_HTTP_CORE_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.CGLIB_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.CHECKSTYLE_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.CXF_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.DROPWIZARD_VERSIONS
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.GATLING_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.GRADLE_VERSIONS_PLUGIN_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.GRADLE_WRAPPER_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.GROOVY_MINIMAL_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.GROOVY_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.GRPC_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.GUAVA_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.HIKARI_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.JACKSON_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.JAVA_POET_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.JOOQ_ADK_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.JOOQ_PLUGIN_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.JOOQ_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.JTWIG_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.JUNIT_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.KAFKA_LOG4J_APPENDER_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.KAFKA_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.KONF_TYPESAFE_CONFIG_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.KONF_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.KOTLIN_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.LOG4J_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.LOG4J_VERSION_JDK6
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.LOGBACK_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.LOGBOOK_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.LOMBOK_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.METRICS_DROPWIZ_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.MICROMETER_JVM_EXTRAS_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.MICROMETER_PROMETHEUS_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.POSTGRES_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.PROJECT_REACTOR_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.PROMETHEUS_DROPWIZARD_SIMPLE_CLIENT
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.PROTOBUF_COMPILATOR_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.PROTOBUF_GRADLE_PLUGIN_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.RESILIENCE4J_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.ROCKSDB_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.RSOCKET_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.SCALA_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.SL4J_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.SPOCK_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.SSH_PLUGIN_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.STAX_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.TOMCAT_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.TYPESAFE_CONFIG_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.WSDL2JAVA_VERSION
import ru.adk.gradle.plugin.constants.configuration.defaults.DefaultDependencyVersions.YAMLBEANS_VERSION

open class ExternalDependencyVersionsConfiguration {
    var cglibVersion = CGLIB_VERSION
    var yamlbeansVersion = YAMLBEANS_VERSION
    var jacksonVersion = JACKSON_VERSION
    var protobufGradlePluginVersion = PROTOBUF_GRADLE_PLUGIN_VERSION
    var protobufCompilatorVersion = PROTOBUF_COMPILATOR_VERSION
    var gradleVersionsPluginVersion = GRADLE_VERSIONS_PLUGIN_VERSION
    var checkstyleVersion = CHECKSTYLE_VERSION
    var typesafeConfigVersion = TYPESAFE_CONFIG_VERSION
    var konfVersion = KONF_VERSION
    var konfTypesafeConfigVersion = KONF_TYPESAFE_CONFIG_VERSION
    var resilience4jVersion = RESILIENCE4J_VERSION
    var metricsDropwizVersion = METRICS_DROPWIZ_VERSION
    var grpcVersion = GRPC_VERSION
    var apacheHttpAsyncClientVersion = APACHE_HTTP_ASYNC_CLIENT_VERSION
    var apacheHttpClientVersion = APACHE_HTTP_CLIENT_VERSION
    var apacheHttpCoreVersion = APACHE_HTTP_CORE_VERSION
    var logbookVersion = LOGBOOK_VERSION
    var logbackVersion = LOGBACK_VERSION
    var log4jVersion = LOG4J_VERSION
    var log4jVersionJdk6 = LOG4J_VERSION_JDK6
    var tomcatVersion = TOMCAT_VERSION
    var groovyVersion = GROOVY_VERSION
    var groovyMinimalVersion = GROOVY_MINIMAL_VERSION
    var jooqVersion = JOOQ_VERSION
    var staxVersion = STAX_VERSION
    var lombokVersion = LOMBOK_VERSION
    var rocksdbVersion = ROCKSDB_VERSION
    var spockVersion = SPOCK_VERSION
    var gradleWrapperVersion = GRADLE_WRAPPER_VERSION
    var micrometerPrometheusVersion = MICROMETER_PROMETHEUS_VERSION
    var micrometerJvmExtrasVersion = MICROMETER_JVM_EXTRAS_VERSION
    var prometheusDropwizardSimpleClient = PROMETHEUS_DROPWIZARD_SIMPLE_CLIENT
    var dropwizardVersions = DROPWIZARD_VERSIONS
    var hikariVersion = HIKARI_VERSION
    var sshPluginVersion = SSH_PLUGIN_VERSION
    var jooqAdkVersion = JOOQ_ADK_VERSION
    var jooqPluginVersion = JOOQ_PLUGIN_VERSION
    var postgresVersion = POSTGRES_VERSION
    var jtwigVersion = JTWIG_VERSION
    var sl4jVersion = SL4J_VERSION
    var cxfVersion = CXF_VERSION
    var wsdl2javaVersion = WSDL2JAVA_VERSION
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
}