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

import java.nio.channels.FileChannel.open
import java.nio.file.Path
import java.nio.file.StandardOpenOption.READ
import java.nio.file.StandardOpenOption.WRITE
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


private val lock = ReentrantLock()
fun <T> Path.withLock(action: () -> T): T = lock.withLock {
    if (!toFile().exists()) {
        return action()
    }
    val directory = parent
    val lockFile = directory.toFile().apply { if (!exists()) mkdirs() }.resolve(toFile().name).apply {
        createNewFile()
        deleteOnExit()
    }
    return open(lockFile.toPath(), READ, WRITE)
            .use { channel -> channel.lock().use { action() } }
            .apply { lockFile.delete() }
}
