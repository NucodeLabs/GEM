@file:JvmName("JavaApi")
package ru.nucodelabs.data.ves

class ExperimentalDataBuilder(source: ExperimentalData) {
    private var ab2: Double = source.ab2
    private var mn2: Double = source.mn2
    private var amperage: Double = source.amperage
    private var voltage: Double = source.voltage
    private var resistanceApparent: Double = source.resistanceApparent
    private var errorResistanceApparent: Double = source.errorResistanceApparent
    private var isHidden: Boolean = source.isHidden

    fun ab2(value: Double) = apply { ab2 = value }
    fun mn2(value: Double) = apply { mn2 = value }
    fun amperage(value: Double) = apply { amperage = value }
    fun voltage(value: Double) = apply { voltage = value }
    fun resistanceApparent(value: Double) = apply { resistanceApparent = value }
    fun errorResistanceApparent(value: Double) = apply { errorResistanceApparent = value }
    fun isHidden(value: Boolean) = apply { isHidden = value }
    fun build() = ExperimentalData(ab2, mn2, amperage, voltage, resistanceApparent, errorResistanceApparent, isHidden)
}

fun copy(source: ExperimentalData) = ExperimentalDataBuilder(source)

class ModelLayerBuilder(source: ModelLayer) {
    private var power = source.power
    private var resistance = source.resistance
    private var isFixedPower = source.isFixedPower
    private var isFixedResistance = source.isFixedResistance

    fun power(value: Double) = apply { power = value }
    fun resistance(value: Double) = apply { resistance = value }
    fun isFixedPower(value: Boolean) = apply { isFixedPower = value }
    fun isFixedResistance(value: Boolean) = apply { isFixedResistance = value }
    fun build() = ModelLayer(power, resistance, isFixedPower, isFixedResistance)
}

fun copy(source: ModelLayer) = ModelLayerBuilder(source)