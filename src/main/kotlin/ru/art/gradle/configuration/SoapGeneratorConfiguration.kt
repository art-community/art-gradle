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
import ru.art.gradle.configuration.SoapGeneratorConfiguration.GenerationMode.*
import ru.art.gradle.constants.*
import ru.art.gradle.context.Context.projectExtension
import javax.inject.*

open class SoapGeneratorConfiguration @Inject constructor(val project: Project) {
    var generationRequests = mutableSetOf<WsdlGenerationRequest>()

    var packageName: String = EMPTY_STRING

    var generationMode: GenerationMode = CLIENT

    fun wsdl(url: String) {
        generationRequests.add(WsdlGenerationRequest(wsdlUrl = url, packageName = packageName.ifBlank { project.projectExtension().generatorConfiguration.packageName }, generationMode = generationMode))
    }

    fun wsdl(url: String, packageName: String) {
        generationRequests.add(WsdlGenerationRequest(wsdlUrl = url, packageName = packageName.ifBlank { project.projectExtension().generatorConfiguration.packageName }, generationMode = generationMode))
    }

    fun wsdl(url: String, packageName: String, generationMode: GenerationMode) {
        generationRequests.add(WsdlGenerationRequest(wsdlUrl = url, packageName = packageName, generationMode = generationMode))
    }

    fun wsdl(url: String, generationMode: GenerationMode) {
        generationRequests.add(WsdlGenerationRequest(wsdlUrl = url, packageName = packageName.ifBlank { project.projectExtension().generatorConfiguration.packageName }, generationMode = generationMode))
    }

    fun clientWsdl(url: String) {
        generationRequests.add(WsdlGenerationRequest(wsdlUrl = url, packageName = packageName.ifBlank { project.projectExtension().generatorConfiguration.packageName }, generationMode = CLIENT))
    }

    fun serverWsdl(url: String) {
        generationRequests.add(WsdlGenerationRequest(wsdlUrl = url, packageName = packageName.ifBlank { project.projectExtension().generatorConfiguration.packageName }, generationMode = SERVER))
    }

    fun clientWsdl(url: String, packageName: String) {
        generationRequests.add(WsdlGenerationRequest(wsdlUrl = url, packageName = packageName, generationMode = CLIENT))
    }

    fun serverWsdl(url: String, packageName: String) {
        generationRequests.add(WsdlGenerationRequest(wsdlUrl = url, packageName = packageName, generationMode = SERVER))
    }

    data class WsdlGenerationRequest(val wsdlUrl: String, val packageName: String, val generationMode: GenerationMode)

    enum class GenerationMode {
        CLIENT,
        SERVER
    }
}