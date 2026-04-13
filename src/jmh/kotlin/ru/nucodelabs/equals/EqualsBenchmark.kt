package ru.nucodelabs.equals

import org.apache.commons.lang3.builder.EqualsBuilder
import org.openjdk.jmh.annotations.*
import ru.nucodelabs.util.Equals

@Suppress("unused")
@Warmup(iterations = 0)
@Measurement(iterations = 2)
@State(Scope.Thread)
open class EqualsBenchmark {
    class Some(
        val a: Int,
        val b: String,
        val c: Double,
        val d: List<String>,
        val e: DoubleArray
    )

    lateinit var objA: Some
    lateinit var objB: Some

    @Setup
    fun setup() {
        objA = Some(1, "12345", 1234.5678, listOf("Some", "String", "Content"), doubleArrayOf(.1, .2, .3))
        objB = Some(1, "12345", 1234.5678, listOf("Some", "String", "Content"), doubleArrayOf(.1, .2, .3))
    }

    @Benchmark
    fun commonUtilsEqualsKt(): Boolean {
        return Equals(objA, objB)
            .by { it.a }
            .by { it.b }
            .by { it.c }
            .by { it.d }
            .and { it, other -> it.e contentEquals other.e }
            .isEqual
    }

    @Benchmark
    fun apacheCommonsEqualsBuilderJava(): Boolean {
        return EqualsBuilder()
            .append(objA.a, objB.a)
            .append(objA.b, objB.b)
            .append(objA.c, objB.c)
            .append(objA.d, objB.d)
            .append(objA.e, objB.e)
            .isEquals
    }
}
