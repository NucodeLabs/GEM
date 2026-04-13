package ru.nucodelabs.geo.ves

import org.junit.jupiter.api.Test

internal class PicketTest {
    @Test
    fun test() {
        val data = ExperimentalData(1.5, 1.0, 1.0, 1.0)
        val picket = Picket(
            experimentalData = listOf(
                data,
                data.copy(ab2 = 2.0),
                data.copy(mn2 = 0.5)
            )
        )
        println(picket.copy(modelData = listOf(ModelLayer(1.0, 1.0))))
    }
}