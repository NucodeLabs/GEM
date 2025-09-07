package ru.nucodelabs.geo.ves

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Size
import ru.nucodelabs.geo.ves.calc.ExperimentalDataSort
import ru.nucodelabs.util.*
import java.util.*

/**
 * Пикет(установка)
 * @property id Уникальный идентификатор
 * @param experimentalData Полевые(экспериментальные) данные
 * @property modelData Данные модели
 * @property offsetX Расстояние до пикета слева
 * @property z Координата Z
 * @property comment Комментарий
 */
class Picket private constructor(
    val name: String,
    experimentalData: List<@Valid ExperimentalData>,
    modelData: List<@Valid ModelLayer>,
    @get:DecimalMin(MIN_OFFSET_X.toString()) val offsetX: Double,
    val z: Double,
    val comment: String,
    skipExperimentalDataProcessing: Boolean = false,
    skipModelDataProcessing: Boolean = false,
) {

    @JsonIgnore
    val id: UUID = UUID.randomUUID()

    @get:Size(max = MAX_MODEL_DATA_SIZE)
    val modelData by lazy {
        if (skipModelDataProcessing) return@lazy modelData
        preprocessModelData(modelData)
    }

    /**
     * Полевые(экспериментальные) данные, отсортированы по AB/2 затем по MN/2
     */
    @get:JsonGetter("experimentalData")
    val sortedExperimentalData: List<ExperimentalData> by lazy {
        if (skipExperimentalDataProcessing) return@lazy experimentalData
        preprocessExperimentalData(experimentalData)
    }

    /**
     * Без отключенных и если одинаковые AB/2, то с наибольшим MN/2
     */
    @get:JsonIgnore
    val effectiveExperimentalData: List<ExperimentalData> by lazy {
        this.sortedExperimentalData.filter { !it.isHidden }
    }

    override fun equals(other: Any?): Boolean {
        return Equals(this, other)
            .by { it.id }
            .by { it.name }
            .by { it.sortedExperimentalData }
            .by { it.modelData }
            .by { it.offsetX }
            .by { it.z }
            .by { it.comment }
            .isEqual
    }

    override fun hashCode(): Int {
        return hash(
            id,
            name,
            sortedExperimentalData,
            modelData,
            offsetX,
            z,
            comment
        )
    }

    /**
     * Slightly optimized copy
     */
    fun copy(
        name: String = this.name,
        experimentalData: List<ExperimentalData> = this.sortedExperimentalData,
        modelData: List<ModelLayer> = this.modelData,
        offsetX: Double = this.offsetX,
        z: Double = this.z,
        comment: String = this.comment
    ): Picket = copied(
        name,
        experimentalData,
        modelData,
        offsetX,
        z,
        comment
    ).okOrThrow { IllegalArgumentException(it.joinToString()) }

    fun copied(
        name: String = this.name,
        experimentalData: List<ExperimentalData> = this.sortedExperimentalData,
        modelData: List<ModelLayer> = this.modelData,
        offsetX: Double = this.offsetX,
        z: Double = this.z,
        comment: String = this.comment
    ): Result<Picket, List<String>> = internalNew(
        name,
        experimentalData,
        modelData,
        offsetX,
        z,
        comment,
        skipExperimentalDataProcessing = this.sortedExperimentalData === experimentalData,
        skipModelDataProcessing = this.modelData === modelData
    )

    private fun preprocessExperimentalData(experimentalData: List<ExperimentalData>): List<ExperimentalData> {
        val acc = mutableListOf<ExperimentalData>()

        // Группируем по AB
        val dupGroups = experimentalData.groupBy { it.ab2 }
        for ((_, group) in dupGroups) {
            // Если больше одного не отключенного дубликата в группе
            acc += if (group.filter { !it.isHidden }.size > 1) {
                val sortedGroup = group.sortedWith(ExperimentalDataSort.orderByDistances)
                List(sortedGroup.size) {
                    // Отключаем все кроме последнего (с наиб. MN)
                    if (it < sortedGroup.lastIndex) {
                        sortedGroup[it].copy(isHidden = true)
                    } else {
                        sortedGroup[it].copy(isHidden = false)
                    }
                }
            } else {
                group
            }
        }

        return acc.sortedWith(ExperimentalDataSort.orderByDistances)
    }

    fun preprocessModelData(raw: List<ModelLayer>): List<ModelLayer> {
        return raw.toMutableList().also {
            if (it.isNotEmpty()) {
                for (i in it.indices) {
                    if (it[i].power.isNaN()) {
                        it[i] = it[i].copy(power = 0.0)
                    }
                }
                it[it.lastIndex] = it.last().copy(power = Double.NaN)
            }
        }
    }

    companion object {
        const val DEFAULT_OFFSET_X = 100.0
        const val DEFAULT_NAME = "Пикет"
        const val DEFAULT_Z = 0.0
        const val DEFAULT_COMMENT = ""

        const val MAX_MODEL_DATA_SIZE = 40
        const val MIN_OFFSET_X = 0.0

        /**
         * Validate and create
         */
        fun new(
            name: String = DEFAULT_NAME,
            experimentalData: List<ExperimentalData> = emptyList(),
            modelData: List<ModelLayer> = emptyList(),
            offsetX: Double = DEFAULT_OFFSET_X,
            z: Double = DEFAULT_Z,
            comment: String = DEFAULT_COMMENT
        ): Result<Picket, List<String>> {
            return internalNew(
                name,
                experimentalData,
                modelData,
                offsetX,
                z,
                comment
            )
        }

        private fun internalNew(
            name: String,
            experimentalData: List<ExperimentalData>,
            modelData: List<ModelLayer>,
            offsetX: Double,
            z: Double,
            comment: String,
            skipExperimentalDataProcessing: Boolean = false,
            skipModelDataProcessing: Boolean = false
        ): Result<Picket, List<String>> {
            val errors = ArrayList<String>()
            validate(modelData.size <= MAX_MODEL_DATA_SIZE) {
                "Model layers count must be ≤ $MAX_MODEL_DATA_SIZE layers"
            }?.let { errors += it }
            validate(offsetX >= MIN_OFFSET_X) { "X-Offset must be zero or positive" }?.let { errors += it }
            if (errors.isNotEmpty()) return Err(errors)
            return Picket(
                name,
                experimentalData,
                modelData,
                offsetX,
                z,
                comment,
                skipExperimentalDataProcessing,
                skipModelDataProcessing
            ).toOkResult()
        }

        /**
         * Classic style initialization. Throws on invalid input.
         *
         * Backwards compatibility.
         */
        operator fun invoke(
            name: String = DEFAULT_NAME,
            experimentalData: List<ExperimentalData> = emptyList(),
            modelData: List<ModelLayer> = emptyList(),
            offsetX: Double = DEFAULT_OFFSET_X,
            z: Double = DEFAULT_Z,
            comment: String = DEFAULT_COMMENT
        ): Picket = new(
            name,
            experimentalData,
            modelData,
            offsetX,
            z,
            comment
        ).okOrThrow { IllegalArgumentException(it.joinToString()) }
    }
}