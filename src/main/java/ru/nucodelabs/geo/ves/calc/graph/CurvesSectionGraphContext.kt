package ru.nucodelabs.geo.ves.calc.graph

import ru.nucodelabs.geo.ves.SectionExperimentalDataSet
import ru.nucodelabs.geo.ves.calc.Bounds
import ru.nucodelabs.geo.ves.calc.picketsBounds
import ru.nucodelabs.util.Point
import kotlin.math.min

class CurvesSectionGraphContext(private val section: SectionExperimentalDataSet) {

    private val rightK = 1.0

    private var resistivityK = 1.0

    private val resistivityValues: MutableList<MutableList<Double>> = arrayListOf()

    init {
        initResistivityValues()
        shiftResistivityValues()
        setK()
        recalculateResistivityValues()
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
            for (abIdx in resistivityValues[picketIdx].indices) {
                val xValue = resistivityValues[picketIdx][abIdx]
                val yValue = pickets[picketIdx].effectiveExperimentalData[abIdx].ab2
                pointsList[picketIdx].add(Point(xValue, yValue))
            }
        }
        return pointsList
    }

    private fun addXValues() {
        val bounds = section.picketsBounds()
        for (picketIdx in resistivityValues.indices) {
            val resistSrc = resistivityValues[picketIdx]
            resistivityValues[picketIdx] = resistSrc.mapTo(ArrayList(resistSrc.size)) { e ->
                e + bounds[picketIdx].leftX
            }
        }
    }

    private fun recalculateResistivityValues() {
        for (picketIdx in resistivityValues.indices) {
            val resistSrc = resistivityValues[picketIdx]
            resistivityValues[picketIdx] = resistSrc.mapTo(ArrayList(resistSrc.size)) { e ->
                e * resistivityK * rightK
            }
        }
    }

    private fun initResistivityValues() {
        val pickets = section.pickets()
        for (picketIdx in pickets.indices) {
            if (pickets[picketIdx].effectiveExperimentalData.isEmpty()) {
                resistivityValues.add(arrayListOf())
                continue
            }
            resistivityValues.add(arrayListOf())
            for (ab in pickets[picketIdx].effectiveExperimentalData) {
                resistivityValues[picketIdx].add(ab.resistivityApparent)
            }
        }
    }

    private fun shiftResistivityValues() {
        for (picketIdx in resistivityValues.indices) {
            val resistSrc = resistivityValues[picketIdx]
            if (resistSrc.isEmpty()) continue
            val minResist = resistSrc.min()
            resistivityValues[picketIdx] = resistSrc.mapTo(ArrayList(resistSrc.size)) { e ->
                e - minResist
            }
        }
    }

    private fun setK() {
        val picketsBounds = section.picketsBounds()
        for (picketIdx in resistivityValues.indices) {
            if (resistivityValues[picketIdx].isEmpty()) continue
            resistivityK = min(resistivityK, getKFor(resistivityValues[picketIdx], picketsBounds[picketIdx]))
        }
    }

    private fun getKFor(resistivity: List<Double>, bound: Bounds): Double {
        val range = resistivity.max() - resistivity.min()
        val xSize = bound.rightX - bound.leftX
        return xSize / range
    }

}