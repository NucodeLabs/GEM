package ru.nucodelabs.algorithms.charts

import ru.nucodelabs.algorithms.forward_solver.ForwardSolver
import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.data.ves.ModelLayer
import javax.inject.Inject

class VesCurvesConverter @Inject constructor(
    val forwardSolver: ForwardSolver
) {
    fun experimentalCurveOf(experimentalData: List<ExperimentalData>): List<Point> {
        if (experimentalData.isEmpty()) {
            return mutableListOf()
        }
        val points: MutableList<Point> = ArrayList()
        for (experimentalDatum in experimentalData) {
            val dotX = experimentalDatum.ab2
            val dotY = experimentalDatum.resistanceApparent.coerceAtLeast(0.0)
            points.add(Point(dotX, dotY))
        }
        return points
    }

    fun experimentalCurveErrorBoundOf(experimentalData: List<ExperimentalData>, boundType: BoundType): List<Point> {
        if (experimentalData.isEmpty()) {
            return mutableListOf()
        }
        val points: MutableList<Point> = mutableListOf()
        for (experimentalDatum in experimentalData) {
            val dotX = experimentalDatum.ab2
            val error = experimentalDatum.errorResistanceApparent / 100.0
            val dotY: Double = if (boundType == BoundType.UPPER_BOUND) {
                (experimentalDatum.resistanceApparent + experimentalDatum.resistanceApparent * error)
                    .coerceAtLeast(0.0)
            } else {
                (experimentalDatum.resistanceApparent - experimentalDatum.resistanceApparent * error)
                    .coerceAtLeast(0.0)
            }
            points.add(Point(dotX, dotY))
        }
        return points
    }

    fun theoreticalCurveOf(experimentalData: List<ExperimentalData>, modelData: List<ModelLayer>): List<Point> {
        if (experimentalData.isEmpty() || modelData.isEmpty()) {
            return mutableListOf()
        }
        val solvedResistance: List<Double> = forwardSolver(experimentalData, modelData)
        val points: MutableList<Point> = mutableListOf()

        for ((index, expData) in experimentalData.withIndex()) {
            val dotX = expData.ab2
            val dotY = solvedResistance[index].coerceAtLeast(0.0)
            points.add(Point(dotX, dotY))
        }
        return points
    }

    fun modelCurveOf(modelData: List<ModelLayer>): List<Point> {
        val firstX = 1e-3
        val lastX = 1e100
        return modelCurveOf(modelData, firstX, lastX)
    }

    fun modelCurveOf(modelData: List<ModelLayer>, firstX: Double, lastX: Double): List<Point> {
        if (modelData.isEmpty()) {
            return mutableListOf()
        }
        val points: MutableList<Point> = mutableListOf()

        // first point
        points.add(Point(firstX, modelData.first().resistance))

        var prevSum = 0.0
        for (i in 0 until modelData.size - 1) {
            val currentResistance = modelData[i].resistance
            val currentPower = modelData[i].power
            points.add(
                Point(
                    currentPower + prevSum,
                    currentResistance
                )
            )
            val nextResistance = modelData[i + 1].resistance
            points.add(
                Point(
                    currentPower + prevSum,
                    nextResistance
                )
            )
            prevSum += currentPower
        }

        // last point
        points.add(
            Point(
                lastX,
                modelData.last().resistance
            )
        )

        return points
    }

    enum class BoundType {
        UPPER_BOUND, LOWER_BOUND
    }
}