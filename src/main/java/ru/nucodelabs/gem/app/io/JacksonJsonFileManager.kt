package ru.nucodelabs.gem.app.io

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import jakarta.inject.Inject
import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.ExperimentalData.Meta.DEFAULT_ERROR
import ru.nucodelabs.geo.ves.ModelLayer
import ru.nucodelabs.geo.ves.calc.rhoA
import java.io.File

internal class JacksonJsonFileManager @Inject constructor(
    objectMapper: ObjectMapper
) : JsonFileManager {

    private val objectMapper = objectMapper
        .addMixIn(ModelLayer::class.java, ModelLayerCompat::class.java)
        .addMixIn(ExperimentalData::class.java, ExperimentalDataCompat::class.java)

    /*
        Backward compatibility:
        resistance -> resistivity
        model last layer power NaN -> 0
    */

    private class ModelLayerPowerCompatDeserializer : StdDeserializer<Double>(Double::class.java) {
        override fun deserialize(
            p: JsonParser?,
            ctxt: DeserializationContext?
        ): Double? {
            p!!
            val value = p.valueAsDouble
            return if (value.isNaN()) .0 else value
        }
    }

    @Suppress("unused")
    private abstract class ModelLayerCompat(
        @param:JsonDeserialize(using = ModelLayerPowerCompatDeserializer::class) val power: Double,
        @param:JsonAlias("resistance") val resistivity: Double,
        val isFixedPower: Boolean = false,
        @param:JsonAlias("isFixedResistance") val isFixedResistivity: Boolean = false
    )

    @Suppress("unused")
    private abstract class ExperimentalDataCompat(
        val ab2: Double,
        val mn2: Double,
        val amperage: Double,
        val voltage: Double,
        @field:JsonAlias("resistanceApparent")
        val resistivityApparent: Double = rhoA(ab2, mn2, amperage, voltage),
        @field:JsonAlias("errorResistanceApparent")
        val errorResistivityApparent: Double = DEFAULT_ERROR,
        val isHidden: Boolean = false
    )


    @Throws(Exception::class)
    override fun <T> loadFromJson(jsonFile: File, type: Class<T>): T =
        objectMapper.readValue(jsonFile, type)

    @Throws(Exception::class)
    override fun <T> saveToJson(jsonFile: File, obj: T) = objectMapper.writeValue(jsonFile, obj)
}