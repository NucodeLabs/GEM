@file:JvmName("JavaApi")
package ru.nucodelabs.geo.ves

class ExperimentalDataBuilder(source: ExperimentalData) {
    private var ab2: Double = source.ab2
    private var mn2: Double = source.mn2
    private var amperage: Double = source.amperage
    private var voltage: Double = source.voltage
    private var resistivityApparent: Double = source.resistivityApparent
    private var errorResistivityApparent: Double = source.errorResistivityApparent
    private var isHidden: Boolean = source.isHidden

    fun ab2(value: Double) = apply { ab2 = value }
    fun mn2(value: Double) = apply { mn2 = value }
    fun amperage(value: Double) = apply { amperage = value }
    fun voltage(value: Double) = apply { voltage = value }
    fun resistivityApparent(value: Double) = apply { resistivityApparent = value }
    fun errorResistivityApparent(value: Double) = apply { errorResistivityApparent = value }
    fun isHidden(value: Boolean) = apply { isHidden = value }
    fun build() = ExperimentalData(ab2, mn2, amperage, voltage, resistivityApparent, errorResistivityApparent, isHidden)
}

fun copy(source: ExperimentalData) = ExperimentalDataBuilder(source)

class ModelLayerBuilder(source: ModelLayer) {
    private var power = source.power
    private var resistivity = source.resistivity
    private var isFixedPower = source.isFixedPower
    private var isFixedResistivity = source.isFixedResistivity

    fun power(value: Double) = apply { power = value }
    fun resistivity(value: Double) = apply { resistivity = value }
    fun isFixedPower(value: Boolean) = apply { isFixedPower = value }
    fun isFixedResistivity(value: Boolean) = apply { isFixedResistivity = value }
    fun build() = ModelLayer(power, resistivity, isFixedPower, isFixedResistivity)
}

fun copy(source: ModelLayer) = ModelLayerBuilder(source)