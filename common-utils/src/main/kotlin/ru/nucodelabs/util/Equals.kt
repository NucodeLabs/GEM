package ru.nucodelabs.util

import java.util.Objects
import kotlin.reflect.KClass

class Equals<T : Any>(
    val thisRef: T,
    val other: Any?
) {
    var isEqual = thisRef === other || other != null && thisRef::class == other::class

    inline fun by(getter: (o: T) -> Any?): Equals<T> {
        if (!isEqual) return this
        @Suppress("unchecked_cast")
        isEqual = isEqual && getter(thisRef) == getter(other as T)
        return this
    }

    inline fun and(condition: (it: T, other: T) -> Boolean): Equals<T> {
        if (!isEqual) return this
        @Suppress("unchecked_cast")
        isEqual = isEqual && condition(thisRef, other as T)
        return this
    }

    inline fun and(condition: () -> Boolean): Equals<T> {
        if (!isEqual) return this
        isEqual = isEqual && condition()
        return this
    }
}

fun hash(vararg values: Any?): Int {
    return Objects.hash(*values)
}