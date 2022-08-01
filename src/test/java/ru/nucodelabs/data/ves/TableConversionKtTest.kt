package ru.nucodelabs.data.ves

import org.junit.jupiter.api.Test

internal class TableConversionKtTest {
    @Test
    fun test() {
        val modelData = listOf(
            ModelLayer(10.0, 20.0),
            ModelLayer(11.0, 20.0),
            ModelLayer(10000.0, 0.0),
            ModelLayer(10.323423, 0.1)
        )

        println(
            modelData.toTabulatedTable()
        )
    }
}