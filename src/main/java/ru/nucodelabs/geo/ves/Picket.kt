package ru.nucodelabs.geo.ves

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
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
    experimentalData: List<ExperimentalData>,
    override val modelData: List<ModelLayer>,
    override val offsetX: Double,
    val z: Double,
    val comment: String,
    skipExperimentalDataProcessing: Boolean = false,
) : ExperimentalDataSet, ModelDataSet {
    constructor(
        name: String = DEFAULT_NAME,
        experimentalData: List<ExperimentalData> = emptyList(),
        modelData: List<ModelLayer> = emptyList(),
        offsetX: Double = DEFAULT_X_OFFSET,
        z: Double = DEFAULT_Z,
        comment: String = DEFAULT_COMMENT
    ) : this(
        name,
        experimentalData,
        modelData,
        offsetX,
        z,
        comment,
        skipExperimentalDataProcessing = false,
    )

    init {
        val errors = listOfNotNull(
            validate(isValidModelDataSize(modelData.size)) {
                InvalidPropertyValue(
                    "modelData.size",
                    "Model layers count must be ≤ $MAX_MODEL_DATA_SIZE layers",
                    modelData.size
                )
            },
            validate(isValidOffsetX(offsetX)) {
                InvalidPropertyValue("offsetX", "X-Offset must be >= $MIN_OFFSET_X", offsetX)
            },
        )
        if (errors.isNotEmpty()) throw InvalidPropertiesException(errors)
    }


    override val modelZ: Double = z

    @JsonIgnore
    val id: UUID = UUID.randomUUID() // todo remove, make name unique

    /**
     * Полевые(экспериментальные) данные, отсортированы по AB/2 затем по MN/2
     */
    @get:JsonGetter("experimentalData")
    override val sortedExperimentalData: List<ExperimentalData> by lazy {
        if (skipExperimentalDataProcessing) return@lazy experimentalData
        toSortedExperimentalData(experimentalData)
    }

    /**
     * Без отключенных и если одинаковые AB/2, то с наибольшим MN/2
     */
    @get:JsonIgnore
    override val effectiveExperimentalData: List<ExperimentalData> by lazy {
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
    ): Picket = Picket(
        name,
        experimentalData,
        modelData,
        offsetX,
        z,
        comment,
        skipExperimentalDataProcessing = this.sortedExperimentalData == experimentalData,
    )

    fun copied(
        name: String = this.name,
        experimentalData: List<ExperimentalData> = this.sortedExperimentalData,
        modelData: List<ModelLayer> = this.modelData,
        offsetX: Double = this.offsetX,
        z: Double = this.z,
        comment: String = this.comment
    ): Result<Picket, List<InvalidPropertyValue>> = try {
        copy(
            name,
            experimentalData,
            modelData,
            offsetX,
            z,
            comment,
        ).toOkResult()
    } catch (e: InvalidPropertiesException) {
        e.errors.toErrorResult()
    }

    companion object Meta {
        const val DEFAULT_X_OFFSET = 100.0
        const val DEFAULT_NAME = "Пикет"
        const val DEFAULT_Z = 0.0
        const val DEFAULT_COMMENT = ""

        const val MAX_MODEL_DATA_SIZE = 40
        const val MIN_OFFSET_X = 0.0

        fun isValidOffsetX(offsetX: Double): Boolean = offsetX >= MIN_OFFSET_X

        fun isValidModelDataSize(modelDataSize: Int): Boolean = modelDataSize <= MAX_MODEL_DATA_SIZE

        fun new(
            name: String = DEFAULT_NAME,
            experimentalData: List<ExperimentalData> = emptyList(),
            modelData: List<ModelLayer> = emptyList(),
            offsetX: Double = DEFAULT_X_OFFSET,
            z: Double = DEFAULT_Z,
            comment: String = DEFAULT_COMMENT
        ): Result<Picket, List<InvalidPropertyValue>> {
            return try {
                Picket(
                    name,
                    experimentalData,
                    modelData,
                    offsetX,
                    z,
                    comment
                ).toOkResult()
            } catch (e: InvalidPropertiesException) {
                e.errors.toErrorResult()
            }
        }
    }
}