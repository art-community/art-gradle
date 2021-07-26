/*
 * ART
 *
 * Copyright 2019-2021 ART
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

package io.art.gradle.common.generator

import io.art.gradle.common.configuration.GeneratorConfiguration
import io.art.gradle.common.configuration.GeneratorMainConfiguration
import io.art.gradle.common.constants.*
import io.art.gradle.common.service.DownloadingRequest
import io.art.gradle.common.service.FileDownloadService.downloadFile

object GeneratorDownloader {
    fun downloadJvmGenerator(configuration: GeneratorMainConfiguration) {
        val request = DownloadingRequest(
                url = JVM_GENERATOR_DOWNLOAD_URL(configuration.repositoryUrl, configuration.version),
                path = configuration.workingDirectory.resolve(JVM_GENERATOR_FILE(configuration.version)),
                lockName = "$GENERATOR$DOT_LOCK",
                timeout = GENERATOR_DOWNLOAD_TIMEOUT
        )
        downloadFile(request)
    }
}
