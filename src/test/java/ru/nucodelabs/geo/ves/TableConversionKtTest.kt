package ru.nucodelabs.geo.ves

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class TableConversionKtTest {
    @Test
    fun test() {
        val modelData = listOf(
            ModelLayer(10.0, 20.0),
            ModelLayer(11.0, 20.0),
            ModelLayer(10000.0, 0.2),
            ModelLayer(10.323423, 0.1)
        )

        assertEquals(
            "10.0 20.0\n" +
                    "11.0 20.0\n" +
                    "10000.0 0.2\n" +
                    "10.323423 0.1",
            modelData.toTabulatedTable()
        )
    }
}