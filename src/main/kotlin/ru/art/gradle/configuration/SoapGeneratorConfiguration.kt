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
import ru.art.generator.soap.model.*
import ru.art.generator.soap.model.SoapGenerationMode.*
import ru.art.gradle.constants.*

open class SoapGeneratorConfiguration constructor(val project: Project) {
    var generationRequests = mutableSetOf<WsdlGenerationRequest>()

    var packageName: String = EMPTY_STRING

    var generationMode: SoapGenerationMode = CLIENT

    fun wsdl(url: String) {
        generationRequests.add(WsdlGenerationRequest(wsdlUrl = url, packageName = packageName, generationMode = generationMode))
    }

    fun wsdl(url: String, packageName: String) {
        generationRequests.add(WsdlGenerationRequest(wsdlUrl = url, packageName = packageName, generationMode = generationMode))
    }

    fun wsdl(url: String, packageName: String, generationMode: SoapGenerationMode) {
        generationRequests.add(WsdlGenerationRequest(wsdlUrl = url, packageName = packageName, generationMode = generationMode))
    }

    data class WsdlGenerationRequest(val wsdlUrl: String, val packageName: String, val generationMode: SoapGenerationMode)
}