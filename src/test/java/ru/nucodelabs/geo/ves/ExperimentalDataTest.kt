package ru.nucodelabs.geo.ves

import org.junit.jupiter.api.Test
import ru.nucodelabs.geo.ves.calc.orderByDistances

internal class ExperimentalDataTest {

    @Test
    fun orderByDistances_() {
        val data = ExperimentalData(1.5, 1.0, 1.0, 1.0)
        val list = listOf(
            data,
            data.copy(ab2 = 2.0),
            data.copy(mn2 = 0.5),
            data.copy(isHidden = true)
        )
        list.sortedWith(orderByDistances()).forEach { println(it) }

        val picket = Picket(experimentalData = list)
        println("initial")
        list.forEach { println(it) }
        println("sorted")
        picket.sortedExperimentalData.forEach { println(it) }
        println("effective")
        picket.effectiveExperimentalData.forEach { println(it) }
    }
}