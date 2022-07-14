package ru.nucodelabs.data.ves

import org.junit.jupiter.api.Test

internal class ExperimentalDataTest {

    @Test
    fun orderByDistances_() {
        val data = ExperimentalData(1.0, 1.0, 1.0, 1.0)
        val list = listOf(
            data,
            data.copy(ab2 = 2.0),
            data.copy(mn2 = 0.5)
        )
        list.sortedWith(orderByDistances()).forEach { println(it) }
    }
}