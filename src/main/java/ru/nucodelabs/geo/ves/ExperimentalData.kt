package ru.nucodelabs.geo.ves

import ru.nucodelabs.geo.ves.calc.rhoA
import ru.nucodelabs.util.Result
import ru.nucodelabs.util.toErrorResult
import ru.nucodelabs.util.toOkResult
import ru.nucodelabs.util.validate

interface ReadOnlyExperimentalSignal {
    val ab2: Double
    val mn2: Double
    val amperage: Double
    val voltage: Double
    val resistanceApparent: Double
    val errorResistanceApparent: Double
    val isHidden: Boolean
}

interface MutableExperimentalSignal : ReadOnlyExperimentalSignal {
    override var ab2: Double
    override var mn2: Double
    override var amperage: Double
    override var voltage: Double
    override var resistanceApparent: Double
    override var errorResistanceApparent: Double
    override var isHidden: Boolean
}

/**
 * Экспериментальное измерение
 * @property ab2 AB/2, м
 * @property mn2 MN/2, м
 * @property amperage Ток, мА
 * @property voltage Напряжение, мВ
 * @property resistanceApparent Сопротивление кажущееся, Ом * м
 * @property errorResistanceApparent Погрешность, %
 * @property isHidden Отключена для интерпретации
 */
data class ExperimentalData(
    override val ab2: Double,
    override val mn2: Double,
    override val amperage: Double,
    override val voltage: Double,
    override val resistanceApparent: Double = rhoA(ab2, mn2, amperage, voltage),
    override val errorResistanceApparent: Double = DEFAULT_ERROR,
    override val isHidden: Boolean = false
) : ReadOnlyExperimentalSignal {

    init {
        val errors = listOfNotNull(
            validateAb2(ab2),
            validateMn2(mn2),
            validateAmperage(amperage),
            validateVoltage(voltage),
            validateResistApparent(resistanceApparent),
            validateErrResistApparent(errorResistanceApparent),
        )
        if (errors.isNotEmpty()) throw InvalidPropertiesException(errors)
    }

    companion object Meta {
        const val DEFAULT_ERROR = 5.0

        const val MIN_DIST = .0
        const val MIN_AMP = .0
        const val MIN_VOLT = .0
        const val MIN_RESIST_APP = 0.1
        const val MIN_ERROR_RESIST_APP = .0
        const val MAX_ERROR_RESIST_APP = 100.0

        const val AB2 = "ab2"
        fun validateAb2(ab2: Double) = validate(isValidDistance(ab2)) {
            InvalidPropertyValue(AB2, "AB/2 must be >= $MIN_DIST", ab2)
        }

        const val MN2 = "mn2"
        fun validateMn2(mn2: Double) = validate(isValidDistance(mn2)) {
            InvalidPropertyValue(MN2, "MN/2 must be >= $MIN_DIST", mn2)
        }

        fun isValidDistance(dist: Double) = dist >= MIN_DIST

        const val AMPERAGE = "amperage"
        fun validateAmperage(amperage: Double) = validate(isValidAmperage(amperage)) {
            InvalidPropertyValue(AMPERAGE, "Amperage must be >= $MIN_AMP", amperage)
        }

        fun isValidAmperage(amperage: Double) = amperage >= MIN_AMP

        const val VOLTAGE = "voltage"
        fun validateVoltage(voltage: Double) = validate(isValidVoltage(voltage)) {
            InvalidPropertyValue(VOLTAGE, "Voltage must be >= $MIN_VOLT", voltage)
        }

        fun isValidVoltage(voltage: Double) = voltage >= MIN_VOLT

        const val RESIST_APP = "resistivityApparent"
        fun validateResistApparent(resistApparent: Double) = validate(isValidResistApparent(resistApparent)) {
            InvalidPropertyValue(RESIST_APP, "Resistivity Apparent must be >= $MIN_RESIST_APP", resistApparent)
        }

        fun isValidResistApparent(resistApparent: Double) = resistApparent >= MIN_RESIST_APP

        const val ERR_RESIST_APP = "errorResistivityApparent"
        fun validateErrResistApparent(errResistApparent: Double) =
            validate(isValidErrResistApparent(errResistApparent)) {
                InvalidPropertyValue(
                    ERR_RESIST_APP,
                    "Error for Resist Apparent must be in range $MIN_ERROR_RESIST_APP - $MAX_ERROR_RESIST_APP %",
                    errResistApparent
                )
            }

        fun isValidErrResistApparent(errResistApparent: Double) =
            errResistApparent in MIN_ERROR_RESIST_APP..MAX_ERROR_RESIST_APP

        fun new(
            ab2: Double,
            mn2: Double,
            amperage: Double,
            voltage: Double,
            resistanceApparent: Double = rhoA(ab2, mn2, amperage, voltage),
            errorResistanceApparent: Double = DEFAULT_ERROR,
            isHidden: Boolean = false
        ): Result<ExperimentalData, List<InvalidPropertyValue>> {
            return try {
                ExperimentalData(
                    ab2,
                    mn2,
                    amperage,
                    voltage,
                    resistanceApparent,
                    errorResistanceApparent,
                    isHidden
                ).toOkResult()
            } catch (e: InvalidPropertiesException) {
                return e.errors.toErrorResult()
            }
        }
    }
}
