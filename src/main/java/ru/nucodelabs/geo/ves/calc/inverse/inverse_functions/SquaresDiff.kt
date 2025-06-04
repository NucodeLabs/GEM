package ru.nucodelabs.geo.ves.calc.inverse.inverse_functions

class SquaresDiff : (List<Double>, List<Double>) -> Double {
    override fun invoke(solvedResistance: List<Double>, experimentalResistance: List<Double>): Double {
        var functionValue = 0.0
        for (i in solvedResistance.indices) {
            functionValue += (solvedResistance[i] - experimentalResistance[i]).let { it * it }
        }
        return kotlin.math.sqrt(functionValue) / solvedResistance.size
    }
}
