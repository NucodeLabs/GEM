package ru.nucodelabs.util

import org.junit.jupiter.api.Test

class EqualsTest {
    class Some(
        val a: Int,
        val b: String,
        val c: Double,
        val d: List<String>
    )

    var objA: Some = Some(1, "12345", 1234.5678, listOf("Some", "String", "Content"))
    var objB: Some = Some(1, "12345", 1234.5678, listOf("Some", "String", "Content"))

    @Test
    fun isEqual() {
        assert(objA !== objB)
        assert(objA != objB)
        val result = Equals(objA, objB)
            .by { it.a }
            .by { it.b }
            .by { it.c }
            .by { it.d }
            .isEqual
        assert(result)
    }

    @Test
    fun isEqual_same() {
        val result = Equals(objA, objB)
            .by { it.a }
            .by { it.b }
            .by { it.c }
            .by { it.d }
            .isEqual
        assert(result)
    }

    @Test
    fun isEqual_not() {
        objB = Some(2, "123", 123.32, emptyList())
        assert(objA !== objB)
        assert(objA != objB)
        val result = Equals(objA, objB)
            .by { it.a }
            .by { it.b }
            .by { it.c }
            .by { it.d }
            .isEqual
        assert(!result)
    }

    @Test
    fun and() {
        val a = doubleArrayOf(.1, .2, .3)
        val b = doubleArrayOf(.1, .2, .3)
        val result = Equals(objA, objB)
            .by { it.a }
            .by { it.b }
            .by { it.c }
            .by { it.d }
            .and { a contentEquals b }
            .isEqual
        assert(result)
    }

    @Test
    fun and_false() {
        val a = doubleArrayOf(.1, .2, .3)
        val b = doubleArrayOf(.1, .2, .3, .4)
        val result = Equals(objA, objB)
            .by { it.a }
            .by { it.b }
            .by { it.c }
            .by { it.d }
            .and { a contentEquals b }
            .isEqual
        assert(!result)
    }
}