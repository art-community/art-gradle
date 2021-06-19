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

package io.art.gradle.common.service

import io.art.gradle.common.constants.lockCreation
import io.art.gradle.common.constants.lockTimeout
import java.lang.Thread.interrupted
import java.net.URL
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption.READ
import java.nio.file.StandardOpenOption.WRITE
import java.time.Duration
import java.time.LocalTime.now
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

data class DownloadingRequest(
        val url: URL,
        val path: Path,
        val lockName: String,
        val lockTimeout: Duration,
)

object FileDownloadService {
    private val downloadLock = ReentrantLock()

    fun downloadFile(request: DownloadingRequest) = request.apply {
        downloadLock.withLock {
            if (path.toFile().exists()) {
                return@withLock
            }
            val directory = path.parent
            val lockFile = directory.toFile().apply { if (!exists()) mkdirs() }.resolve(lockName).apply { deleteOnExit() }
            val channel = FileChannel.open(lockFile.toPath(), READ, WRITE)
            val lock = channel.lock()
            val time = now().plus(lockTimeout)
            try {
                while (lockFile.exists() && !lock.isValid && !interrupted()) {
                    if (now().isAfter(time)) {
                        throw lockTimeout()
                    }
                }

                if (!lockFile.createNewFile() || !lock.isValid) {
                    throw lockCreation()
                }

                url.openStream().use { input ->
                    path.toFile().outputStream().use { output ->
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE * 2)
                        var read: Int
                        while (input.read(buffer, 0, DEFAULT_BUFFER_SIZE * 2).also { read = it } >= 0) {
                            output.write(buffer, 0, read)
                        }
                    }
                }
            } finally {
                lock.release()
                channel.close()
                lockFile.delete()
            }
        }
    }

}
