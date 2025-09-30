package ru.nucodelabs.geo.ves

import ru.nucodelabs.util.Result
import ru.nucodelabs.util.toErrorResult
import ru.nucodelabs.util.toOkResult
import ru.nucodelabs.util.validate

interface ReadOnlyModelLayer {
    val power: Double
    val resistance: Double
    val isFixedPower: Boolean
    val isFixedResistance: Boolean
}

interface MutableModelLayer : ReadOnlyModelLayer {
    override var power: Double
    override var resistance: Double
    override var isFixedPower: Boolean
    override var isFixedResistance: Boolean
}

/**
 * Слой модели
 * @property power Мощность, м
 * @property resistance Сопротивление, Ом * м
 * @property isFixedPower Значение зафиксировано для обратной задачи
 * @property isFixedResistance Значение зафиксировано для обратной задачи
 */
data class ModelLayer(
    override val power: Double,
    override val resistance: Double,
    override val isFixedPower: Boolean = false,
    override val isFixedResistance: Boolean = false
) : ReadOnlyModelLayer {
    init {
        val errors = listOfNotNull(
            validatePower(power),
            validateResistivity(resistance)
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

        fun validateResistivity(resist: Double) = validate(isValidResist(resist)) {
            InvalidPropertyValue("resistivity", "Resistivity=$resist must be >= $MIN_RESIST", resist)
        }

        fun isValidResist(resist: Double) = resist >= MIN_RESIST

        fun from(
            modelLayer: ReadOnlyModelLayer
        ) = new(
            power = modelLayer.power,
            resistance = modelLayer.resistance,
            isFixedPower = modelLayer.isFixedPower,
            isFixedResistance = modelLayer.isFixedResistance
        )

        fun new(
            power: Double,
            resistance: Double,
            isFixedPower: Boolean = false,
            isFixedResistance: Boolean = false
        ): Result<ModelLayer, List<InvalidPropertyValue>> {
            return try {
                ModelLayer(
                    power,
                    resistance,
                    isFixedPower,
                    isFixedResistance
                ).toOkResult()
            } catch (e: InvalidPropertiesException) {
                e.errors.toErrorResult()
            }
        }
    }
}
