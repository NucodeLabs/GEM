package ru.nucodelabs.geo.ves.calc.graph

import ru.nucodelabs.geo.forward.ForwardSolver
import ru.nucodelabs.geo.ves.ExperimentalDataSet
import ru.nucodelabs.geo.ves.ModelDataSet
import ru.nucodelabs.geo.ves.calc.adapter.invoke
import ru.nucodelabs.geo.ves.calc.resistanceApparentLowerBoundByError
import ru.nucodelabs.geo.ves.calc.resistanceApparentUpperBoundByError
import ru.nucodelabs.util.Point

fun experimentalCurve(picket: ExperimentalDataSet) =
    picket.effectiveExperimentalData.map { Point(it.ab2, it.resistanceApparent) }

fun experimentalCurveErrorUpperBound(picket: ExperimentalDataSet) =
    picket.effectiveExperimentalData.map { Point(it.ab2, it.resistanceApparentUpperBoundByError) }

fun experimentalCurveErrorLowerBound(picket: ExperimentalDataSet) =
    picket.effectiveExperimentalData.map { Point(it.ab2, it.resistanceApparentLowerBoundByError) }

fun experimentalHiddenPoints(picket: ExperimentalDataSet) =
    picket.sortedExperimentalData.filter { it.isHidden }.map { Point(it.ab2, it.resistanceApparent) }

fun theoreticalCurve(
    experimental: ExperimentalDataSet,
    modelDataSet: ModelDataSet,
    forwardSolver: ForwardSolver
): List<Point> {
    val sortedExp = experimental.sortedExperimentalData
    if (sortedExp.isEmpty() || modelDataSet.modelData.isEmpty()) {
        return listOf()
    }
    val solvedResistance = forwardSolver(sortedExp, modelDataSet.modelData)
    return List(sortedExp.size) { i ->
        Point(sortedExp[i].ab2, solvedResistance[i])
    }
}

fun modelStepGraph(picket: ModelDataSet, beginX: Double = 1e-3, endX: Double = 1e100): List<Point> {
    val modelData = picket.modelData

    if (modelData.isEmpty()) {
        return mutableListOf()
    }
    val points = mutableListOf<Point>()

    // first point
    points += Point(beginX, modelData.first().resistance)

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