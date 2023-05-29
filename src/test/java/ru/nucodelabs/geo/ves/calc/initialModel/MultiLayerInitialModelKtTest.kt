package ru.nucodelabs.geo.ves.calc.initialModel

import org.junit.jupiter.api.Test
import ru.nucodelabs.ShiraPicket
import ru.nucodelabs.geo.target.SquareDiffTargetFunction
import ru.nucodelabs.geo.ves.calc.forward.ForwardSolver

class MultiLayerInitialModelKtTest {

    private val targetFunction = SquareDiffTargetFunction()
    private val forwardSolver = ForwardSolver()
    private val signals = ShiraPicket.picket.effectiveExperimentalData

    @Test
    fun multiLayerInitialModel_test() {
        multiLayerInitialModel(
                targetFunction = targetFunction,
                forwardSolver = forwardSolver,
                signals = signals,
                breakAfterFoundResult = false
        ) { model, targetFunctionValue, isResult ->
//            model.forEachIndexed { idx, layer -> println("$idx: power = ${layer.power} :: res = ${layer.resistance}") }
            println("${model.size}\t$targetFunctionValue\t$isResult")
        }

//        println("RESULT:")
//        result.forEachIndexed { idx, layer -> println("$idx: power = ${layer.power} :: res = ${layer.resistance}") }

//        println(objectMapper.writeValueAsString(result))
    }
}