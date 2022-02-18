package io.art.gradle.common.constants

import UnixSourceDependency

const val AUTOGEN = "autogen.sh"
const val CONFIGURE = "configure"
const val MAKE = "make"
const val MAKE_FILE = "Makefile"

fun bashCommand(arguments: Array<String>) = arrayOf("bash", "-c", arguments.joinToString(" "))

val LCX = UnixSourceDependency("lxc").apply {
    url("https://github.com/lxc/lxc")
    configureOptions("--disable-doc")
    parallel()
}
