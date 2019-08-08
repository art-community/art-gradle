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

package ru.art.gradle.configuration

import org.gradle.api.*
import ru.art.gradle.constants.*
import ru.art.gradle.dependency.*
import javax.inject.*

open class ModulesConfiguration @Inject constructor(val project: Project) {
    val modules: MutableSet<Dependency> = mutableSetOf()
    var version: String? = null
        private set

    fun useVersion(version: String) {
        this.version = version
    }

    private fun addModule(module: String, dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        val dependency = Dependency(ART_MODULE_GROUP, module)
        dependencyModifiers.forEach { modifier -> modifier(dependency) }
        modules.find { current -> current.group == dependency.group && current.artifact == dependency.artifact }
                ?.let { current -> current.version = dependency.version }
                ?: modules.add(dependency)
    }

    protected open fun applicationCore(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-core", dependencyModifiers)
    }

    protected open fun applicationConfig(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-config", dependencyModifiers)
    }

    protected open fun applicationConfigExtensions(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-config-extensions", dependencyModifiers)
    }

    protected open fun applicationConfigGroovy(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-config-groovy", dependencyModifiers)
    }

    protected open fun applicationConfigRemote(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-config-remote", dependencyModifiers)
    }

    protected open fun applicationConfigRemoteApi(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-config-remote-api", dependencyModifiers)
    }

    protected open fun applicationConfigTypesafe(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-config-typesafe", dependencyModifiers)
    }

    protected open fun applicationConfigYaml(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-config-yaml", dependencyModifiers)
    }

    protected open fun applicationConfigurator(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-configurator", dependencyModifiers)
    }

    protected open fun applicationConfiguratorApi(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-configurator-api", dependencyModifiers)
    }

    protected open fun applicationEntity(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-entity", dependencyModifiers)
    }

    protected open fun applicationExample(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-example", dependencyModifiers)
    }

    protected open fun applicationExampleApi(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-example-api", dependencyModifiers)
    }

    protected open fun applicationGenerator(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-generator", dependencyModifiers)
    }

    protected open fun applicationHttp(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-http", dependencyModifiers)
    }

    protected open fun applicationHttpClient(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-http-client", dependencyModifiers)
    }

    protected open fun applicationHttpJson(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-http-json", dependencyModifiers)
    }

    protected open fun applicationHttpXml(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-http-xml", dependencyModifiers)
    }

    protected open fun applicationHttpServer(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-http-server", dependencyModifiers)
    }

    protected open fun applicationJson(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-json", dependencyModifiers)
    }

    protected open fun applicationKafkaConsumer(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-kafka-consumer", dependencyModifiers)
    }

    protected open fun applicationKafkaProducer(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-kafka-producer", dependencyModifiers)
    }

    protected open fun applicationLogging(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-logging", dependencyModifiers)
    }

    protected open fun applicationMetrics(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-metrics", dependencyModifiers)
    }

    protected open fun applicationMetricsHttp(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-metrics-http", dependencyModifiers)
    }

    protected open fun applicationModuleExecutor(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-module-executor", dependencyModifiers)
    }

    protected open fun applicationNetworkManager(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-network-manager", dependencyModifiers)
    }

    protected open fun applicationPlatform(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-platform", dependencyModifiers)
    }

    protected open fun applicationPlatformApi(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-platform-api", dependencyModifiers)
    }

    protected open fun applicationProtobuf(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-protobuf", dependencyModifiers)
    }

    protected open fun applicationGrpc(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-grpc", dependencyModifiers)
    }

    protected open fun applicationGrpcClient(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-grpc-client", dependencyModifiers)
    }

    protected open fun applicationGrpcServer(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-grpc-server", dependencyModifiers)
    }

    protected open fun applicationProtobufGenerated(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-protobuf-generated", dependencyModifiers)
    }

    protected open fun applicationReactiveService(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-reactive-service", dependencyModifiers)
    }

    protected open fun applicationRemoteScheduler(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-remote-scheduler", dependencyModifiers)
    }

    protected open fun applicationRemoteSchedulerApi(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-remote-scheduler-api", dependencyModifiers)
    }

    protected open fun applicationRocksDb(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-rocks-db", dependencyModifiers)
    }

    protected open fun applicationRsocket(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-rsocket", dependencyModifiers)
    }

    protected open fun applicationScheduler(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-scheduler", dependencyModifiers)
    }

    protected open fun applicationSchedulerDbAdapterApi(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-scheduler-db-adapter-api", dependencyModifiers)
    }

    protected open fun applicationService(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-service", dependencyModifiers)
    }

    protected open fun applicationSoap(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-soap", dependencyModifiers)
    }

    protected open fun applicationSoapClient(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-soap-client", dependencyModifiers)
    }

    protected open fun applicationSoapServer(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-soap-server", dependencyModifiers)
    }

    protected open fun applicationSql(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-sql", dependencyModifiers)
    }

    protected open fun applicationState(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-state", dependencyModifiers)
    }

    protected open fun applicationStateApi(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-state-api", dependencyModifiers)
    }

    protected open fun applicationTarantool(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-tarantool", dependencyModifiers)
    }

    protected open fun applicationXml(dependencyModifiers: Array<out (dependency: Dependency) -> Unit> = emptyArray()) {
        addModule("application-xml", dependencyModifiers)
    }
}

open class PublicModulesConfiguration @Inject constructor(project: Project) : ModulesConfiguration(project) {
    public override fun applicationConfig(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationConfig(dependencyModifiers)
    }

    public override fun applicationConfigExtensions(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationConfigExtensions(dependencyModifiers)
    }

    public override fun applicationConfigGroovy(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationConfigGroovy(dependencyModifiers)
    }

    public override fun applicationConfigRemote(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationConfigRemote(dependencyModifiers)
    }

    public override fun applicationConfigRemoteApi(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationConfigRemoteApi(dependencyModifiers)
    }

    public override fun applicationConfigTypesafe(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationConfigTypesafe(dependencyModifiers)
    }

    public override fun applicationConfigYaml(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationConfigYaml(dependencyModifiers)
    }

    public override fun applicationConfigurator(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationConfigurator(dependencyModifiers)
    }

    public override fun applicationConfiguratorApi(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationConfiguratorApi(dependencyModifiers)
    }

    public override fun applicationCore(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationCore(dependencyModifiers)
    }

    public override fun applicationEntity(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationEntity(dependencyModifiers)
    }

    public override fun applicationExample(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationExample(dependencyModifiers)
    }

    public override fun applicationExampleApi(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationExampleApi(dependencyModifiers)
    }

    public override fun applicationGenerator(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationGenerator(dependencyModifiers)
    }

    public override fun applicationHttp(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationHttp(dependencyModifiers)
    }

    public override fun applicationHttpClient(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationHttpClient(dependencyModifiers)
    }

    public override fun applicationHttpJson(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationHttpJson(dependencyModifiers)
    }

    public override fun applicationHttpXml(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationHttpXml(dependencyModifiers)
    }

    public override fun applicationHttpServer(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationHttpServer(dependencyModifiers)
    }

    public override fun applicationJson(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationJson(dependencyModifiers)
    }

    public override fun applicationKafkaConsumer(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationKafkaConsumer(dependencyModifiers)
    }

    public override fun applicationKafkaProducer(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationKafkaProducer(dependencyModifiers)
    }

    public override fun applicationLogging(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationLogging(dependencyModifiers)
    }

    public override fun applicationMetrics(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationMetrics(dependencyModifiers)
    }

    public override fun applicationMetricsHttp(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationMetricsHttp(dependencyModifiers)
    }

    public override fun applicationModuleExecutor(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationModuleExecutor(dependencyModifiers)
    }

    public override fun applicationNetworkManager(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationNetworkManager(dependencyModifiers)
    }

    public override fun applicationPlatform(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationPlatform(dependencyModifiers)
    }

    public override fun applicationPlatformApi(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationPlatformApi(dependencyModifiers)
    }

    public override fun applicationProtobuf(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationProtobuf(dependencyModifiers)
    }

    public override fun applicationGrpc(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationGrpc(dependencyModifiers)
    }

    public override fun applicationGrpcClient(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationGrpcClient(dependencyModifiers)
    }

    public override fun applicationProtobufGenerated(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationProtobufGenerated(dependencyModifiers)
    }

    public override fun applicationGrpcServer(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationGrpcServer(dependencyModifiers)
    }

    public override fun applicationReactiveService(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationReactiveService(dependencyModifiers)
    }

    public override fun applicationRemoteScheduler(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationRemoteScheduler(dependencyModifiers)
    }

    public override fun applicationRemoteSchedulerApi(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationRemoteSchedulerApi(dependencyModifiers)
    }

    public override fun applicationRocksDb(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationRocksDb(dependencyModifiers)
    }

    public override fun applicationRsocket(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationRsocket(dependencyModifiers)
    }

    public override fun applicationScheduler(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationScheduler(dependencyModifiers)
    }

    public override fun applicationSchedulerDbAdapterApi(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationSchedulerDbAdapterApi(dependencyModifiers)
    }

    public override fun applicationService(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationService(dependencyModifiers)
    }

    public override fun applicationSoap(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationSoap(dependencyModifiers)
    }

    public override fun applicationSoapClient(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationSoapClient(dependencyModifiers)
    }

    public override fun applicationSoapServer(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationSoapServer(dependencyModifiers)
    }

    public override fun applicationSql(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationSql(dependencyModifiers)
    }

    public override fun applicationState(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationState(dependencyModifiers)
    }

    public override fun applicationStateApi(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationStateApi(dependencyModifiers)
    }

    public override fun applicationTarantool(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationTarantool(dependencyModifiers)
    }

    public override fun applicationXml(vararg dependencyModifiers: (dependency: Dependency) -> Unit) {
        super.applicationXml(dependencyModifiers)
    }
}

open class ModulesCombinationConfiguration @Inject constructor(project: Project) : PublicModulesConfiguration(project) {
    private var protocolsConfiguration = ProtocolsConfiguration(project)
    private var dbConfiguration = DatabasesConfiguration(project)
    private var dataFormatsConfiguration = DataFormatsConfiguration(project)

    fun core(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        applicationCore(*dependencyModifiers)
        applicationEntity(*dependencyModifiers)
    }

    fun logging(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        core(*dependencyModifiers)
        applicationLogging(*dependencyModifiers)
    }

    fun metrics(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        core(*dependencyModifiers)
        applicationMetrics(*dependencyModifiers)
    }

    fun kafkaConsumer(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        logging(*dependencyModifiers)
        applicationService(*dependencyModifiers)
        applicationKafkaConsumer(*dependencyModifiers)
    }

    fun kafkaProducer(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        logging(*dependencyModifiers)
        applicationService(*dependencyModifiers)
        applicationKafkaProducer(*dependencyModifiers)
    }

    fun kafka(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        kafkaConsumer(*dependencyModifiers)
        kafkaProducer(*dependencyModifiers)
    }

    fun localConfigurationManagement(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        logging(*dependencyModifiers)
        applicationConfig(*dependencyModifiers)
        applicationConfigYaml(*dependencyModifiers)
        applicationConfigGroovy(*dependencyModifiers)
        applicationConfigTypesafe(*dependencyModifiers)
    }

    fun remoteConfigurationManagement(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        logging(*dependencyModifiers)
        applicationService(*dependencyModifiers)
        applicationConfigRemote(*dependencyModifiers)
        applicationConfigRemoteApi(*dependencyModifiers)
        applicationConfiguratorApi(*dependencyModifiers)
        applicationProtobuf(*dependencyModifiers)
        applicationProtobufGenerated(*dependencyModifiers)
    }

    fun configurationManagement(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        localConfigurationManagement(*dependencyModifiers)
        remoteConfigurationManagement(*dependencyModifiers)
        applicationConfigExtensions(*dependencyModifiers)
    }

    fun configurator(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        applicationConfigurator(*dependencyModifiers)
    }

    fun configuratorApi(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        core(*dependencyModifiers)
        applicationService(*dependencyModifiers)
        applicationConfiguratorApi(*dependencyModifiers)
    }

    fun state(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        applicationState(*dependencyModifiers)
    }

    fun clientBalancing(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        protocols {
            grpcServer(*dependencyModifiers)
        }
        applicationNetworkManager(*dependencyModifiers)
        applicationStateApi(*dependencyModifiers)
    }

    fun localScheduling(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        logging(*dependencyModifiers)
        applicationScheduler(*dependencyModifiers)
    }

    fun remoteScheduling(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        protocols {
            grpcServer(*dependencyModifiers)
        }
        applicationRemoteSchedulerApi(*dependencyModifiers)
    }

    fun remoteScheduler(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        applicationRemoteScheduler(*dependencyModifiers)
    }

    fun moduleExecutor(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        applicationModuleExecutor(*dependencyModifiers)
    }

    fun example(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        applicationExample(*dependencyModifiers)
    }

    fun exampleApi(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        applicationExampleApi(*dependencyModifiers)
    }

    fun scheduling(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        localScheduling(*dependencyModifiers)
        remoteScheduling(*dependencyModifiers)
    }

    fun reactive(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        logging(*dependencyModifiers)
        applicationService(*dependencyModifiers)
        applicationReactiveService(*dependencyModifiers)
    }

    fun dataFormats(configurator: DataFormatsConfiguration.() -> Unit) {
        configurator(dataFormatsConfiguration)
        modules += dataFormatsConfiguration.modules
    }

    fun protocols(configurator: ProtocolsConfiguration.() -> Unit) {
        configurator(protocolsConfiguration)
        modules += protocolsConfiguration.modules
    }

    fun db(configurator: DatabasesConfiguration.() -> Unit) {
        configurator(dbConfiguration)
        modules += dbConfiguration.modules
    }

    fun kit(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        kafka(*dependencyModifiers)
        configurationManagement(*dependencyModifiers)
        scheduling(*dependencyModifiers)
        protocols {
            httpServer(*dependencyModifiers)
            grpcServer(*dependencyModifiers)
            rsocket(*dependencyModifiers)
            soapServer(*dependencyModifiers)
            httpCommunication(*dependencyModifiers)
            grpcCommunication(*dependencyModifiers)
            soapCommunication(*dependencyModifiers)
        }
        db {
            sql(*dependencyModifiers)
            tarantool(*dependencyModifiers)
            rocks(*dependencyModifiers)
        }
    }
}

open class ProtocolsConfiguration @Inject constructor(project: Project) : ModulesConfiguration(project) {
    fun httpServer(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        applicationCore(dependencyModifiers)
        applicationEntity(dependencyModifiers)
        applicationLogging(dependencyModifiers)
        applicationMetrics(dependencyModifiers)
        applicationService(dependencyModifiers)
        applicationJson(dependencyModifiers)
        applicationXml(dependencyModifiers)
        applicationHttp(dependencyModifiers)
        applicationHttpJson(dependencyModifiers)
        applicationHttpXml(dependencyModifiers)
        applicationHttpServer(dependencyModifiers)
        applicationMetricsHttp(dependencyModifiers)
    }

    fun grpcServer(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        applicationCore(dependencyModifiers)
        applicationEntity(dependencyModifiers)
        applicationLogging(dependencyModifiers)
        applicationService(dependencyModifiers)
        applicationProtobuf(dependencyModifiers)
        applicationProtobufGenerated(dependencyModifiers)
        applicationGrpc(dependencyModifiers)
        applicationGrpcServer(dependencyModifiers)
    }

    fun rsocket(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        applicationCore(dependencyModifiers)
        applicationEntity(dependencyModifiers)
        applicationLogging(dependencyModifiers)
        applicationReactiveService(dependencyModifiers)
        applicationService(dependencyModifiers)
        applicationJson(dependencyModifiers)
        applicationProtobuf(dependencyModifiers)
        applicationXml(dependencyModifiers)
        applicationProtobufGenerated(dependencyModifiers)
    }

    fun soapServer(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        applicationCore(dependencyModifiers)
        applicationEntity(dependencyModifiers)
        applicationLogging(dependencyModifiers)
        applicationMetrics(dependencyModifiers)
        applicationService(dependencyModifiers)
        applicationXml(dependencyModifiers)
        applicationHttp(dependencyModifiers)
        applicationHttpXml(dependencyModifiers)
        applicationHttpServer(dependencyModifiers)
        applicationMetricsHttp(dependencyModifiers)
        applicationSoap(dependencyModifiers)
        applicationSoapServer(dependencyModifiers)
    }

    fun httpCommunication(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        applicationCore(dependencyModifiers)
        applicationEntity(dependencyModifiers)
        applicationLogging(dependencyModifiers)
        applicationService(dependencyModifiers)
        applicationJson(dependencyModifiers)
        applicationHttp(dependencyModifiers)
        applicationHttpJson(dependencyModifiers)
        applicationHttpXml(dependencyModifiers)
        applicationHttpClient(dependencyModifiers)
    }

    fun grpcCommunication(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        applicationCore(dependencyModifiers)
        applicationEntity(dependencyModifiers)
        applicationLogging(dependencyModifiers)
        applicationService(dependencyModifiers)
        applicationProtobuf(dependencyModifiers)
        applicationProtobufGenerated(dependencyModifiers)
        applicationGrpc(dependencyModifiers)
        applicationGrpcClient(dependencyModifiers)
    }

    fun soapCommunication(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        applicationCore(dependencyModifiers)
        applicationEntity(dependencyModifiers)
        applicationLogging(dependencyModifiers)
        applicationService(dependencyModifiers)
        applicationHttp(dependencyModifiers)
        applicationHttpXml(dependencyModifiers)
        applicationHttpClient(dependencyModifiers)
        applicationSoap(dependencyModifiers)
        applicationSoapClient(dependencyModifiers)
        applicationXml(dependencyModifiers)
    }
}

open class DataFormatsConfiguration @Inject constructor(project: Project) : ModulesConfiguration(project) {
    fun json(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        applicationCore(dependencyModifiers)
        applicationEntity(dependencyModifiers)
        applicationLogging(dependencyModifiers)
        applicationJson(dependencyModifiers)
    }

    fun xml(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        applicationCore(dependencyModifiers)
        applicationEntity(dependencyModifiers)
        applicationXml(dependencyModifiers)
    }

    fun protobuf(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        applicationCore(dependencyModifiers)
        applicationEntity(dependencyModifiers)
        applicationLogging(dependencyModifiers)
        applicationProtobuf(dependencyModifiers)
        applicationProtobufGenerated(dependencyModifiers)
    }
}

open class DatabasesConfiguration @Inject constructor(project: Project) : ModulesConfiguration(project) {
    fun sql(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        applicationCore(dependencyModifiers)
        applicationEntity(dependencyModifiers)
        applicationLogging(dependencyModifiers)
        applicationSql(dependencyModifiers)
    }

    fun tarantool(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        applicationCore(dependencyModifiers)
        applicationEntity(dependencyModifiers)
        applicationLogging(dependencyModifiers)
        applicationTarantool(dependencyModifiers)
    }

    fun rocks(vararg dependencyModifiers: (dependency: Dependency) -> Unit = emptyArray()) {
        applicationCore(dependencyModifiers)
        applicationEntity(dependencyModifiers)
        applicationLogging(dependencyModifiers)
        applicationProtobuf(dependencyModifiers)
        applicationProtobufGenerated(dependencyModifiers)
        applicationRocksDb(dependencyModifiers)
    }
}
