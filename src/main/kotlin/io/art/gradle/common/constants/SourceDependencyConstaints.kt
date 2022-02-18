package io.art.gradle.common.constants

import UnixSourceDependency
import org.gradle.internal.os.OperatingSystem
import java.io.File


const val SOURCES = "sources"

const val AUTOGEN_SCRIPT = "./autogen.sh"
const val CONFIGURE_SCRIPT = "./configure"
const val MAKE = "make"
const val MAKE_FILE = "Makefile"
val CMAKE = "cmake".binaryResolve().absolutePath
val CMAKE_BUILD = "$CMAKE --build . "

const val CMAKE_BUILD_TYPE_DEBUG = "-DCMAKE_BUILD_TYPE=Debug"
const val CMAKE_BUILD_TYPE_RELEASE = "-DCMAKE_BUILD_TYPE=Release"
const val CMAKE_BUILD_TYPE_RELEASE_WITH_DEBUG = "-DCMAKE_BUILD_TYPE=RelWithDebInfo"

const val CMAKE_BUILD_CONFIG_DEBUG = "--config Debug"
const val CMAKE_BUILD_CONFIG_RELEASE = "--config Release"
const val CMAKE_BUILD_CONFIG_RELEASE_WITH_DEBUG = "--config RelWithDebInfo"

const val DOS_TO_UNIX_FILE = "test /usr/bin/dos2unix && /usr/bin/dos2unix "
const val BACKWARD_SLASH = "\\"
const val BACKWARD_SLASH_REGEX = "\\\\"
const val WINDOWS_DISK_PATH_SLASH = ":/"
const val WINDOWS_DISK_PATH_BACKWARD_SLASH = ":\\"
const val WINDOWS_DISK_PATH_BACKWARD_SLASH_REGEX = ":\\\\"
const val WSL_DISK_PREFIX = "/mnt/"

fun bashCommand(vararg arguments: String) = arrayOf("bash", "-c", arguments.joinToString(" "))

fun command(vararg arguments: String) = arrayOf(arguments.joinToString(" "))

fun builtinLxc(static: Boolean) = UnixSourceDependency("lxc").apply {
    url("https://github.com/lxc/lxc")
    configureOptions("--disable-doc")
    if (static) configureOptions("--enable-static")
    parallel()
    copy("src/lxc/.libs/liblxc.a", "src/main/resources")
}

fun String.binaryResolve(): File {
    if (!OperatingSystem.current().isWindows) return File(this)
    val process = Runtime.getRuntime().exec("where $this")
    process.inputStream.reader().use { reader ->
        return File(reader.readLines().first())
    }
}

fun String.wslResolve(): String {
    var converted = this
    if (!OperatingSystem.current().isWindows) return converted
    if (converted.isEmpty()) return converted
    if (SLASH == EMPTY_STRING + converted[0] || BACKWARD_SLASH == EMPTY_STRING + converted[0]) {
        converted = converted.substring(1)
    }
    if (converted.contains(WINDOWS_DISK_PATH_SLASH) || converted.contains(WINDOWS_DISK_PATH_BACKWARD_SLASH)) {
        converted = converted
                .replace(WINDOWS_DISK_PATH_SLASH.toRegex(), SLASH)
                .replace(WINDOWS_DISK_PATH_BACKWARD_SLASH_REGEX.toRegex(), SLASH)
                .replace(BACKWARD_SLASH_REGEX.toRegex(), SLASH)
        val firstLetter: String = EMPTY_STRING + converted[0]
        return WSL_DISK_PREFIX + firstLetter.toLowerCase() + converted.substring(1)
    }
    return converted
}
