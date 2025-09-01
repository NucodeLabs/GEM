package ru.nucodelabs.geo.ves.calc.graph

import ru.nucodelabs.geo.forward.ForwardSolver
import ru.nucodelabs.geo.ves.Picket
import ru.nucodelabs.geo.ves.calc.adapter.invoke
import ru.nucodelabs.geo.ves.calc.resistanceApparentLowerBoundByError
import ru.nucodelabs.geo.ves.calc.resistanceApparentUpperBoundByError
import ru.nucodelabs.util.Point

fun experimentalCurve(picket: Picket) =
    picket.effectiveExperimentalData.map { Point(it.ab2, it.resistanceApparent) }


fun experimentalCurveErrorUpperBound(picket: Picket) =
    picket.effectiveExperimentalData.map { Point(it.ab2, it.resistanceApparentUpperBoundByError) }

fun experimentalCurveErrorLowerBound(picket: Picket) =
    picket.effectiveExperimentalData.map { Point(it.ab2, it.resistanceApparentLowerBoundByError) }

fun experimentalHiddenPoints(picket: Picket) =
    picket.sortedExperimentalData.filter { it.isHidden }.map { Point(it.ab2, it.resistanceApparent) }

fun theoreticalCurve(picket: Picket, forwardSolver: ForwardSolver): List<Point> {
    if (picket.sortedExperimentalData.isEmpty() || picket.modelData.isEmpty()) {
        return listOf()
    }
    val solvedResistance = forwardSolver(picket.sortedExperimentalData, picket.modelData)
    return List(picket.sortedExperimentalData.size) { i ->
        Point(picket.sortedExperimentalData[i].ab2, solvedResistance[i])
    }
}

fun misfits(picket: Picket, misfitsFunction: MisfitsFunction): List<Double> =
    misfitsFunction(picket.effectiveExperimentalData, picket.modelData)

fun modelStepGraph(picket: Picket, beginX: Double = 1e-3, endX: Double = 1e100): List<Point> {
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