package ru.nucodelabs.util

sealed interface Result<TYPE, ERROR>

class Ok<T, E>(val value: T) : Result<T, E>

class Err<T, E>(val error: E) : Result<T, E>

fun <T, E> T.toOkResult(): Result<T, E> = Ok(this)
fun <T, E> E.toErrorResult(): Result<T, E> = Err(this)

inline fun <T, E> Result<T, E>.okOrThrow(
    mapError: (E) -> Throwable = { IllegalStateException(it.toString()) }
): T {
    return when (this) {
        is Ok -> value
        is Err -> throw mapError(error)
    }
}

inline fun <T, E> Result<T, E>.ifError(action: (E) -> Unit): Result<T, E> {
    if (this is Err) action(error)
    return this
}

inline fun <T, E> Result<T, E>.ifOk(action: (T) -> Unit): Result<T, E> {
    if (this is Ok) action(value)
    return this
}