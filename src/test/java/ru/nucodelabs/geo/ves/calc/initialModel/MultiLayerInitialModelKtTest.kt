package ru.nucodelabs.geo.ves.calc.initialModel

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import ru.nucodelabs.ShiraPicket
import ru.nucodelabs.geo.ves.calc.forward.ForwardSolver

class MultiLayerInitialModelKtTest {

    private val forwardSolver = ForwardSolver()
    private val signals = ShiraPicket.picket.effectiveExperimentalData

    val objectMapper = jacksonObjectMapper()

    @Test
    fun multiLayerInitialModel_test() {
        val result = multiLayerInitialModel(
            forwardSolver = forwardSolver,
            signals = signals
        ) { model, targetFunctionValue ->
            model.forEachIndexed { idx, layer -> println("$idx: power = ${layer.power} :: res = ${layer.resistance}") }
            println(targetFunctionValue)
        }

        println(objectMapper.writeValueAsString(result))
    }
}