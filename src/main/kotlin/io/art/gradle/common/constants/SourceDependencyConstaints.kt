package io.art.gradle.common.constants

import UnixSourceDependency

const val AUTOGEN_SCRIPT = "./autogen.sh"
const val CONFIGURE_SCRIPT = "./configure"
const val MAKE = "make"
const val MAKE_FILE = "Makefile"
const val DOS_TO_UNIX_FILE = "/usr/bin/dos2unix"

fun bashCommand(arguments: Array<String>) = arrayOf("bash", "-c", arguments.joinToString(" "))

fun builtinLxc() = UnixSourceDependency("lxc").apply {
    url("https://github.com/lxc/lxc")
    configureOptions("--disable-doc", "--static")
    parallel()
    copy("src/lxc/.libs/liblxc.a", "src/resources/liblxc.a")
}
