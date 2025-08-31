package ru.nucodelabs.geo.ves.calc.graph

import jakarta.inject.Inject
import ru.nucodelabs.geo.ves.Section
import ru.nucodelabs.geo.ves.calc.Bounds
import ru.nucodelabs.geo.ves.calc.picketsBounds
import ru.nucodelabs.util.Point
import kotlin.math.min

class CurvesSectionGraphContext @Inject constructor(inputSection: Section) {

    private val section = inputSection.copy()

    private val rightK = 1.0

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
            if (section.pickets[picketIdx].effectiveExperimentalData.isEmpty()) {
                pointsList.add(arrayListOf())
                continue
            }
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
            if (section.pickets[picketIdx].effectiveExperimentalData.isEmpty()) {
                resistances.add(arrayListOf())
                continue
            }
            resistances.add(arrayListOf())
            for (ab in section.pickets[picketIdx].effectiveExperimentalData) {
                resistances[picketIdx].add(ab.resistanceApparent)
            }
        }
    }

    private fun shiftResistances() {
        for (picketIdx in resistances.indices) {
            if (resistances[picketIdx].isEmpty())
                continue
            val minResistance = resistances[picketIdx].min()
            resistances[picketIdx] = resistances[picketIdx].map { e -> e - minResistance } as MutableList<Double>
        }
    }

    private fun setK() {
        for (picketIdx in resistances.indices) {
            if (resistances[picketIdx].isEmpty())
                continue
            resistanceK = min(resistanceK, getKFor(resistances[picketIdx], section.picketsBounds()[picketIdx]))
        }
    }

    private fun getKFor(resistances: List<Double>, bound: Bounds): Double {
        val range = resistances.max() - resistances.min()
        val xSize = bound.rightX - bound.leftX
        return xSize / range
    }

}