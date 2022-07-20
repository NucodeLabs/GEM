package ru.nucodelabs.algorithms.charts.curves_chart

import ru.nucodelabs.algorithms.charts.Point
import ru.nucodelabs.data.ves.Bounds
import ru.nucodelabs.data.ves.Section
import ru.nucodelabs.data.ves.picketsBounds
import javax.inject.Inject
import kotlin.math.min

class CurvesChartParser @Inject constructor(inputSection: Section) {

    private val section = inputSection.copy()

    private val rightK = 0.95

    private var resistanceK = 1.0

    private val resistances: MutableList<MutableList<Double>> = arrayListOf()

    init {
        initResistances()
        shiftResistances()
        setK()
        recalculateResistances()
        addXValues()
    }

    fun getPoints(): List<List<Point>> {
        val pointsList: MutableList<MutableList<Point>> = arrayListOf()
        for (picketIdx in section.pickets.indices) {
            if (section.pickets[picketIdx].effectiveExperimentalData.isEmpty())
                continue
            pointsList.add(arrayListOf())
            for (abIdx in resistances[picketIdx].indices) {
                val xValue = resistances[picketIdx][abIdx]
                val yValue = section.pickets[picketIdx].effectiveExperimentalData[abIdx].ab2
                pointsList[picketIdx].add(Point(xValue, yValue))
            }
        }
        return pointsList
    }

    private fun addXValues() {
        for (picketIdx in resistances.indices) {
            resistances[picketIdx] =
                resistances[picketIdx].map { e -> e + section.picketsBounds()[picketIdx].leftX } as MutableList<Double>
        }
    }

    private fun recalculateResistances() {
        for (picketIdx in resistances.indices) {
            resistances[picketIdx] =
                resistances[picketIdx].map { e -> e * resistanceK * rightK } as MutableList<Double>
        }
    }

    private fun initResistances() {
        for (picketIdx in section.pickets.indices) {
            if (section.pickets[picketIdx].effectiveExperimentalData.isEmpty())
                continue
            resistances.add(arrayListOf())
            for (ab in section.pickets[picketIdx].effectiveExperimentalData) {
                resistances[picketIdx].add(ab.resistanceApparent)
            }
        }
    }

    private fun shiftResistances() {
        for (picketIdx in resistances.indices) {
            val minResistance = resistances[picketIdx].min()
            resistances[picketIdx] = resistances[picketIdx].map { e -> e - minResistance } as MutableList<Double>
        }
    }

    private fun setK() {
        for (picketIdx in resistances.indices) {
            resistanceK = min(resistanceK, getKFor(resistances[picketIdx], section.picketsBounds()[picketIdx]))
        }
    }

    private fun getKFor(resistances: List<Double>, bound: Bounds): Double {
        val range = resistances.max() - resistances.min()
        val xSize = bound.rightX - bound.leftX
        return xSize / range
    }

}