package ru.nucodelabs.geo.ves.calc.inverse.map

import org.apache.commons.math3.optim.InitialGuess
import org.apache.commons.math3.optim.MaxEval
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer
import ru.nucodelabs.geo.map.Point

class InverseSolver(private val redPoints: List<Point>) {
    //Размер симплекса (по каждому измерению)
    private val SIDE_LENGTH_DEFAULT = 1.0

    //Какие-то константы для SimplexOptimize
    private val RELATIVE_THRESHOLD_DEFAULT = 1e-10
    private val ABSOLUTE_THRESHOLD_DEFAULT = 1e-30
    private val MAX_EVAL = 100000
    private val DIMENSIONS_COUNT = 4

    fun getOptimizedAngles(initialPoints: Pair<Point, Point>): Pair<Point, Point> {
        val optimizer = SimplexOptimizer(RELATIVE_THRESHOLD_DEFAULT, ABSOLUTE_THRESHOLD_DEFAULT)

        val startPoint = listOf(
            initialPoints.first.x.toDouble(),
            initialPoints.first.y.toDouble(),
            initialPoints.second.x.toDouble(),
            initialPoints.second.y.toDouble()
        ).toDoubleArray()

        val multivariateFunction = FunctionValue(redPoints)

        val initialGuess = InitialGuess(startPoint)

        val nelderMeadSimplex = NelderMeadSimplex(DIMENSIONS_COUNT, SIDE_LENGTH_DEFAULT)

        val pointValuePair = optimizer.optimize(
            MaxEval(MAX_EVAL),
            ObjectiveFunction(multivariateFunction),
            GoalType.MINIMIZE,
            initialGuess,
            nelderMeadSimplex
        )

        val key = pointValuePair.key

        return Pair(Point(key[0].toInt(), key[1].toInt()), Point(key[2].toInt(), key[3].toInt()))
    }

}