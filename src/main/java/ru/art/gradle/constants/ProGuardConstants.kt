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

package ru.art.gradle.constants

const val PRO_GUARD_TASK = "proGuardTransform"
const val OPTIMIZED_JAR_POSTFIX = "-optimized.jar"
const val RU_ART_CLASSES_SPECIFICATION = "class ru.art.** { *; }"
val KEEP_ATTRIBUTES = arrayOf("*Annotation*", "Signature", "InnerClasses", "EnclosingMethod")