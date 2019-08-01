package ru.art.exception

fun <R> ignoreException(action: () -> R, orElse: () -> R): R = try {
    action()
} catch (e: Exception) {
    orElse()
}

fun ignoreException(action: () -> Unit) = try {
    action()
} catch (e: Exception) {

}
