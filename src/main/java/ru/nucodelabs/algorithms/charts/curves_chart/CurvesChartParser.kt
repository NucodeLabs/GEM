package ru.nucodelabs.algorithms.charts.curves_chart

import ru.nucodelabs.algorithms.charts.Point
import ru.nucodelabs.algorithms.charts.VesCurvesConverter
import ru.nucodelabs.data.ves.Bounds
import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.data.ves.Section
import ru.nucodelabs.data.ves.picketsBounds
import javax.inject.Inject
import kotlin.properties.Delegates

class CurvesChartParser @Inject constructor(inputSection: Section, private val vesCurvesConverter: VesCurvesConverter) {

    private val section = inputSection.copy()

    private val rightK = 0.95

    private var resistanceK by Delegates.notNull<Double>()

    private val resistances: MutableList<MutableList<Double>> = arrayListOf()

    fun getPoints(): List<List<Point>> {

        return arrayListOf()
    }

    private fun initResistances() {
        for (picketIdx in section.pickets.indices) {
            resistances.add(arrayListOf())
            for (ab in section.pickets[picketIdx].sortedExperimentalData) {
                resistances[picketIdx].add(ab.resistanceApparent)
            }
        }
    }

    private fun shiftResistances() {
        for (picket in resistances) {
            for (resistance in picket) {

            }
        }
    }

    private fun setK() {
        val bounds = section.picketsBounds()
        var k = 1.0
        for (picketIdx in section.pickets.indices) {
            val resistances = section.pickets[picketIdx].sortedExperimentalData.map { it.resistanceApparent }

        }
    }

    private fun getKFor(resistances: List<Double>, bound: Bounds): Double {
        val range = resistances.max() - resistances.min()
        val xSize = bound.rightX - bound.leftX
        return xSize / range
    }

}