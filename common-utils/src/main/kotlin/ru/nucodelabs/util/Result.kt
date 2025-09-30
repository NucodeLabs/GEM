package ru.nucodelabs.util

sealed interface Result<TYPE, ERROR>

@JvmInline
value class Ok<T, E>(val value: T) : Result<T, E>

@JvmInline
value class Err<T, E>(val error: E) : Result<T, E>

fun <T, E> T.toOkResult(): Result<T, E> = Ok(this)
fun <T, E> E.toErrorResult(): Result<T, E> = Err(this)

@Suppress("unused")
inline fun <T, E> Result<T, E>.okOrThrow(
    mapError: (E) -> Throwable = { IllegalStateException(it.toString()) }
): T {
    return when (this) {
        is Ok -> value
        is Err -> throw mapError(error)
    }
}

@Suppress("unused")
inline fun <T, E> Result<T, E>.ifError(action: (E) -> Unit): Result<T, E> {
    if (this is Err) action(error)
    return this
}

@Suppress("unused")
inline fun <T, E> Result<T, E>.ifOk(action: (T) -> Unit): Result<T, E> {
    if (this is Ok) action(value)
    return this
}