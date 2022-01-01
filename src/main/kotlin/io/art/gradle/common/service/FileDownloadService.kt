/*
 * ART
 *
 * Copyright 2019-2022 ART
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

package io.art.gradle.common.service

import java.net.URL
import java.nio.file.Path
import java.time.Duration
import java.util.concurrent.CompletableFuture.runAsync
import java.util.concurrent.TimeUnit.MILLISECONDS

data class DownloadingRequest(
        val url: URL,
        val path: Path,
        val lockName: String,
        val timeout: Duration,
)

object FileDownloadService {
    private const val bufferSize = DEFAULT_BUFFER_SIZE * 2

    fun downloadFile(request: DownloadingRequest) {
        runAsync {
            request.apply {
                request.path.parent.resolve(request.lockName).withLock {
                    url.openStream().use { input ->
                        path.toFile().outputStream().use { output ->
                            val buffer = ByteArray(bufferSize)
                            var read: Int
                            while (input.read(buffer, 0, bufferSize).also { byte -> read = byte } >= 0) {
                                output.write(buffer, 0, read)
                            }
                        }
                    }
                }
            }
        }.get(request.timeout.toMillis(), MILLISECONDS)
    }
}
