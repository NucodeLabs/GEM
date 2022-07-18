package ru.nucodelabs.algorithms.charts

import ru.nucodelabs.algorithms.forward_solver.ForwardSolver
import ru.nucodelabs.data.ves.Picket
import ru.nucodelabs.data.ves.resistanceApparentLowerBoundByError
import ru.nucodelabs.data.ves.resistanceApparentUpperBoundByError

class VesCurvesContext(val picket: Picket) {
    val experimentalCurve by lazy {
        picket.effectiveExperimentalData.map { Point(it.ab2, it.resistanceApparent) }
    }

    val experimentalCurveErrorUpperBound by lazy {
        picket.effectiveExperimentalData.map { Point(it.ab2, it.resistanceApparentUpperBoundByError) }
    }

    val experimentalCurveErrorLowerBound by lazy {
        picket.effectiveExperimentalData.map { Point(it.ab2, it.resistanceApparentLowerBoundByError) }
    }

    val experimentalHiddenPoints by lazy {
        picket.sortedExperimentalData.filter { it.isHidden }.map { Point(it.ab2, it.resistanceApparent) }
    }

    fun theoreticalCurveBy(forwardSolver: ForwardSolver): List<Point> {
        if (picket.sortedExperimentalData.isEmpty() || picket.modelData.isEmpty()) {
            return listOf()
        }
        val solvedResistance = forwardSolver(picket.sortedExperimentalData, picket.modelData)
        return List(picket.sortedExperimentalData.size) { i ->
            Point(picket.sortedExperimentalData[i].ab2, solvedResistance[i])
        }
    }

    fun misfitsBy(misfitsFunction: MisfitsFunction): List<Double> =
        misfitsFunction(picket.effectiveExperimentalData, picket.modelData)

    fun modelStepGraph(beginX: Double = 1e-3, endX: Double = 1e100): List<Point> {
        val modelData = picket.modelData

        if (modelData.isEmpty()) {
            return mutableListOf()
        }
        val points = mutableListOf<Point>()

        // first point
        points += Point(beginX, picket.modelData.first().resistance)

        var prevSum = 0.0
        for (i in 0 until modelData.size - 1) {
            val currentResistance = modelData[i].resistance
            val currentPower = modelData[i].power

            points += Point(
                currentPower + prevSum,
                currentResistance
            )

            val nextResistance = modelData[i + 1].resistance
            points += Point(
                currentPower + prevSum,
                nextResistance
            )
            prevSum += currentPower
        }

        // last point
        points += Point(
            endX,
            modelData.last().resistance
        )

        return points
    }
}

val Picket.vesCurvesContext
    get() = VesCurvesContext(this)
