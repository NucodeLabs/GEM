package ru.nucodelabs.util

sealed interface Result<TYPE, ERROR>

class Ok<T, E>(val value: T) : Result<T, E>

class Err<T, E>(val error: E) : Result<T, E>

fun <T, E> T.toOkResult(): Result<T, E> = Ok(this)

inline fun <T, E> Result<T, E>.okOrThrow(
    mapError: (E) -> Throwable = { IllegalStateException(it.toString()) }
): T {
    return when (this) {
        is Ok -> value
        is Err -> throw mapError(error)
    }
}

inline fun <T, E> Result<T, E>.onError(onError: (E) -> Unit) {
    when (this) {
        is Err -> onError(error)
        else -> return
    }
}