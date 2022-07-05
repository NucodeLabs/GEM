package ru.nucodelabs.algorithms.forward_solver

import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.data.ves.ModelLayer

internal class SonetForwardSolver : ForwardSolver {

    init {
        System.loadLibrary("forwardsolver")
    }

    private external fun ves(
        resistance: DoubleArray,
        power: DoubleArray,
        layersCnt: Int,
        AB_2: DoubleArray,
        distCnt: Int
    ): DoubleArray

    override fun invoke(experimentalData: List<ExperimentalData>, modelData: List<ModelLayer>): List<Double> =
        ves(
            resistance = modelData.map { it.resistance }.toDoubleArray(),
            power = modelData.map { it.power }.toDoubleArray(),
            layersCnt = modelData.size,
            AB_2 = experimentalData.map { it.ab2 }.toDoubleArray(),
            distCnt = experimentalData.size
        ).toList()
}