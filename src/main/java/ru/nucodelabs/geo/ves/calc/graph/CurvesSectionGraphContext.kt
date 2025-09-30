package ru.nucodelabs.geo.ves.calc.graph

import ru.nucodelabs.geo.ves.SectionExperimentalDataSet
import ru.nucodelabs.geo.ves.calc.Bounds
import ru.nucodelabs.geo.ves.calc.picketsBounds
import ru.nucodelabs.util.Point
import kotlin.math.min

class CurvesSectionGraphContext(private val section: SectionExperimentalDataSet) {

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
        val pickets = section.pickets()
        for (picketIdx in pickets.indices) {
            if (pickets[picketIdx].effectiveExperimentalData.isEmpty()) {
                pointsList.add(arrayListOf())
                continue
            }
            pointsList.add(arrayListOf())
            for (abIdx in resistances[picketIdx].indices) {
                val xValue = resistances[picketIdx][abIdx]
                val yValue = pickets[picketIdx].effectiveExperimentalData[abIdx].ab2
                pointsList[picketIdx].add(Point(xValue, yValue))
            }
        }
        return pointsList
    }

    private fun addXValues() {
        val bounds = section.picketsBounds()
        for (picketIdx in resistances.indices) {
            val resistSrc = resistances[picketIdx]
            resistances[picketIdx] = resistSrc.mapTo(ArrayList(resistSrc.size)) { e ->
                e + bounds[picketIdx].leftX
            }
        }
    }

    private fun recalculateResistances() {
        for (picketIdx in resistances.indices) {
            val resistSrc = resistances[picketIdx]
            resistances[picketIdx] = resistSrc.mapTo(ArrayList(resistSrc.size)) { e ->
                e * resistanceK * rightK
            }
        }
    }

    private fun initResistances() {
        val pickets = section.pickets()
        for (picketIdx in pickets.indices) {
            if (pickets[picketIdx].effectiveExperimentalData.isEmpty()) {
                resistances.add(arrayListOf())
                continue
            }
            resistances.add(arrayListOf())
            for (ab in pickets[picketIdx].effectiveExperimentalData) {
                resistances[picketIdx].add(ab.resistanceApparent)
            }
        }
    }

    private fun shiftResistances() {
        for (picketIdx in resistances.indices) {
            val resistSrc = resistances[picketIdx]
            if (resistSrc.isEmpty()) continue
            val minResistance = resistSrc.min()
            resistances[picketIdx] = resistSrc.mapTo(ArrayList(resistSrc.size)) { e ->
                e - minResistance
            }
        }
    }

    private fun setK() {
        val picketsBounds = section.picketsBounds()
        for (picketIdx in resistances.indices) {
            if (resistances[picketIdx].isEmpty()) continue
            resistanceK = min(resistanceK, getKFor(resistances[picketIdx], picketsBounds[picketIdx]))
        }
    }

    private fun getKFor(resistances: List<Double>, bound: Bounds): Double {
        val range = resistances.max() - resistances.min()
        val xSize = bound.rightX - bound.leftX
        return xSize / range
    }

}