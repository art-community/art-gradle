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

package io.art.gradle.external.constants

import org.gradle.api.GradleException
import org.gradle.internal.os.OperatingSystem

fun unsupportedGraalOs(os: OperatingSystem) = GradleException("Unsupported GraalVM OS: $os")

fun unsupportedGraalArchitecture(architecture: String) = GradleException("Unsupported GraalVM architecture: $architecture")

fun graalWindowsVSVarsPathIsEmpty() = GradleException("""
    You are using GraalVM on Windows. Please, specify windowsVisualStudioVarsScript(<existed path>) in section 'art { executable { native {} } }' or pass path with property -PwindowsVisualStudioVars=<existed path>
    Example: 
        art {
            executable {
                native {
                    windowsVisualStudioVars("D:/Development/Microsoft Visual Studio/VC/Auxiliary/Build/vcvars64.bat")
                }
            }
        }
""".trimIndent())

fun unableToFindGraalUpdater() = GradleException("Unable to find 'gu' executable for GraalVM")

fun unableToLockGraalDownloader() = GradleException("Unable to create lock file for GraalVM downloader")

fun graalDownloadTimeout() = GradleException("Unable to download GraalVM during $GRAAL_DOWNLOAD_TIMEOUT")

const val GRAAL_VM_JDK_8_DARWIN_WARING = "GraalVM for JDK 8 on MacOS not supported"
