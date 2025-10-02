package ru.nucodelabs.geo.ves.calc.graph

import ru.nucodelabs.geo.forward.ForwardSolver
import ru.nucodelabs.geo.ves.ExperimentalDataSet
import ru.nucodelabs.geo.ves.ModelDataSet
import ru.nucodelabs.geo.ves.calc.adapter.invoke
import ru.nucodelabs.geo.ves.calc.resistivityApparentLowerBoundByError
import ru.nucodelabs.geo.ves.calc.resistivityApparentUpperBoundByError
import ru.nucodelabs.util.Point

fun experimentalCurve(picket: ExperimentalDataSet) =
    picket.effectiveExperimentalData.map { Point(it.ab2, it.resistivityApparent) }

fun experimentalCurveErrorUpperBound(picket: ExperimentalDataSet) =
    picket.effectiveExperimentalData.map { Point(it.ab2, it.resistivityApparentUpperBoundByError) }

fun experimentalCurveErrorLowerBound(picket: ExperimentalDataSet) =
    picket.effectiveExperimentalData.map { Point(it.ab2, it.resistivityApparentLowerBoundByError) }

fun experimentalHiddenPoints(picket: ExperimentalDataSet) =
    picket.sortedExperimentalData.filter { it.isHidden }.map { Point(it.ab2, it.resistivityApparent) }

fun theoreticalCurve(
    experimental: ExperimentalDataSet,
    modelDataSet: ModelDataSet,
    forwardSolver: ForwardSolver
): List<Point> {
    val sortedExp = experimental.sortedExperimentalData
    if (sortedExp.isEmpty() || modelDataSet.modelData.isEmpty()) {
        return listOf()
    }
    val solvedResist = forwardSolver(sortedExp, modelDataSet.modelData)
    return List(sortedExp.size) { i ->
        Point(sortedExp[i].ab2, solvedResist[i])
    }
}

fun modelStepGraph(picket: ModelDataSet, beginX: Double = 1e-3, endX: Double = 1e100): List<Point> {
    val modelData = picket.modelData

    if (modelData.isEmpty()) {
        return mutableListOf()
    }
    val points = mutableListOf<Point>()

    // first point
    points += Point(beginX, modelData.first().resistivity)

    var prevSum = 0.0
    for (i in 0 until modelData.size - 1) {
        val currentResist = modelData[i].resistivity
        val currentPower = modelData[i].power

        points += Point(
            currentPower + prevSum,
            currentResist
        )

        val nextResist = modelData[i + 1].resistivity
        points += Point(
            currentPower + prevSum,
            nextResist
        )
        prevSum += currentPower
    }

    // last point
    points += Point(
        endX,
        modelData.last().resistivity
    )

    return points
}