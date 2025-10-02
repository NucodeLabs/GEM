package ru.nucodelabs.geo.ves

import ru.nucodelabs.util.Result
import ru.nucodelabs.util.toErrorResult
import ru.nucodelabs.util.toOkResult
import ru.nucodelabs.util.validate

interface ReadOnlyModelLayer {
    val power: Double
    val resistivity: Double
    val isFixedPower: Boolean
    val isFixedResistivity: Boolean
}

interface MutableModelLayer : ReadOnlyModelLayer {
    override var power: Double
    override var resistivity: Double
    override var isFixedPower: Boolean
    override var isFixedResistivity: Boolean
}

/**
 * Слой модели
 * @property power Мощность, м
 * @property resistivity Сопротивление, Ом * м
 * @property isFixedPower Значение зафиксировано для обратной задачи
 * @property isFixedResistivity Значение зафиксировано для обратной задачи
 */
data class ModelLayer(
    override val power: Double,
    override val resistivity: Double,
    override val isFixedPower: Boolean = false,
    override val isFixedResistivity: Boolean = false
) : ReadOnlyModelLayer {
    init {
        val errors = listOfNotNull(
            validatePower(power),
            validateResistivity(resistivity)
        )
        if (errors.isNotEmpty()) throw InvalidPropertiesException(errors)
    }

    companion object Meta {
        const val MIN_POWER = .0
        const val MIN_RESIST = 0.1

        fun validatePower(power: Double) = validate(isValidPower(power)) {
            InvalidPropertyValue("power", "Power=$power must be >= $MIN_POWER", power)
        }

        fun isValidPower(power: Double): Boolean = power >= MIN_POWER

        fun validateResistivity(resist: Double) = validate(isValidResistivity(resist)) {
            InvalidPropertyValue("resistivity", "Resistivity=$resist must be >= $MIN_RESIST", resist)
        }

        fun isValidResistivity(resist: Double) = resist >= MIN_RESIST

        fun from(
            modelLayer: ReadOnlyModelLayer
        ) = new(
            power = modelLayer.power,
            resistivity = modelLayer.resistivity,
            isFixedPower = modelLayer.isFixedPower,
            isFixedResistivity = modelLayer.isFixedResistivity
        )

        fun new(
            power: Double,
            resistivity: Double,
            isFixedPower: Boolean = false,
            isFixedResistivity: Boolean = false
        ): Result<ModelLayer, List<InvalidPropertyValue>> {
            return try {
                ModelLayer(
                    power,
                    resistivity,
                    isFixedPower,
                    isFixedResistivity
                ).toOkResult()
            } catch (e: InvalidPropertiesException) {
                e.errors.toErrorResult()
            }
        }
    }
}
