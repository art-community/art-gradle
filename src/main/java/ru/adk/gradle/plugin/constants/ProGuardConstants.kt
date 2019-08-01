package ru.adk.gradle.plugin.constants

const val PRO_GUARD_TASK = "proGuardTransform"
const val OPTIMIZED_JAR_POSTFIX = "-optimized.jar"
const val RU_ADK_CLASSES_SPECIFICATION = "class ru.adk.** { *; }"
const val RU_RTI_CLASSES_SPECIFICATION = "class ru.rti.** { *; }"
val KEEP_ATTRIBUTES = arrayOf("*Annotation*", "Signature", "InnerClasses", "EnclosingMethod")