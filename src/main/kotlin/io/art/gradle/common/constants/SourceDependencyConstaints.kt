package io.art.gradle.common.constants

import UnixSourceDependency

const val SOURCES = "sources"

const val AUTOGEN_SCRIPT = "./autogen.sh"
const val CONFIGURE_SCRIPT = "./configure"
const val MAKE = "make"
const val MAKE_FILE = "Makefile"
const val CMAKE = "cmake"
const val CMAKE_BUILD = "--build"

const val CMAKE_CACHE = "CMakeCache.txt"

const val CMAKE_BUILD_TYPE_DEBUG = "-DCMAKE_BUILD_TYPE=Debug"
const val CMAKE_BUILD_TYPE_RELEASE = "-DCMAKE_BUILD_TYPE=Release"
const val CMAKE_BUILD_TYPE_RELEASE_WITH_DEBUG = "-DCMAKE_BUILD_TYPE=RelWithDebInfo"

const val CMAKE_BUILD_CONFIG_OPTION = "--config"
const val CMAKE_BUILD_CONFIG_DEBUG = "Debug"
const val CMAKE_BUILD_CONFIG_RELEASE = "Release"
const val CMAKE_BUILD_CONFIG_RELEASE_WITH_DEBUG = "RelWithDebInfo"

const val DOS_TO_UNIX_FILE = "test /usr/bin/dos2unix && /usr/bin/dos2unix "

fun preconfiguredLxc(static: Boolean) = UnixSourceDependency("lxc").apply {
    url("https://github.com/lxc/lxc")
    configureOptions("--disable-doc")
    if (static) configureOptions("--enable-static-binaries")
    parallel()
    copy("src/lxc/.libs/liblxc.a", "src/main/resources")
}
