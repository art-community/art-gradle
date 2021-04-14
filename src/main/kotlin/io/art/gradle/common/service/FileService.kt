/*
 * ART
 *
 * Copyright 2020 ART
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

import org.gradle.api.GradleException
import java.nio.charset.Charset
import java.nio.file.Path

fun fileCreationException(path: Path) = GradleException("Unable to create file: $path")


fun Path.touch(): Path {
    if (toFile().exists()) return this
    if (!toFile().mkdirs()) {
        throw fileCreationException(parent)
    }
    return this
}

fun Path.writeContent(content: String): Path {
    if (parent.toFile().exists()) {
        toFile().writeText(content, charset = Charset.defaultCharset())
        return this
    }
    if (!parent.toFile().mkdirs()) {
        throw fileCreationException(parent)
    }
    toFile().writeText(content, charset = Charset.defaultCharset())
    return this
}
