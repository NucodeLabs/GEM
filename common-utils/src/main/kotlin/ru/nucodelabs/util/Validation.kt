package ru.nucodelabs.util

/**
 * Return null if valid else error object
 */
inline fun <T> validate(condition: Boolean, lazyError: () -> T): T? {
    if (condition) return null
    return lazyError()
}