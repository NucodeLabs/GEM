package ru.nucodelabs.algorithms.error

import org.junit.jupiter.api.Test

internal class SchlumbergerErrorFunctionsKtTest {
    @Test
    fun check() {
        measureError(10.0, 0.0, 0.0).also { println(it) }
        kWithError(10.0, 20.0, 0.0, 0.0).also { println(it) }
    }
}