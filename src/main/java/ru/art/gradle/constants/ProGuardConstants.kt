package ru.art.gradle.constants

const val PRO_GUARD_TASK = "proGuardTransform"
const val OPTIMIZED_JAR_POSTFIX = "-optimized.jar"
const val RU_ART_CLASSES_SPECIFICATION = "class ru.art.** { *; }"
val KEEP_ATTRIBUTES = arrayOf("*Annotation*", "Signature", "InnerClasses", "EnclosingMethod")