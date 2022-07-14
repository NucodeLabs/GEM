package ru.nucodelabs.data.ves

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.util.*

/**
 * Пикет(установка)
 * @property id Уникальный идентификатор
 * @param experimentalData Полевые(экспериментальные) данные
 * @property modelData Данные модели
 * @property offsetX Расстояние до пикета слева
 * @property z Координата Z
 */
data class Picket(
    @JsonIgnore val id: UUID = UUID.randomUUID(),
    val name: String = "Пикет",
    private val experimentalData: List<ExperimentalData> = listOf(),
    @field:Size(max = 40) val modelData: List<ModelLayer> = listOf(),
    @field:Min(0) val offsetX: Double = 100.0,
    val z: Double = 0.0
) {
    /**
     * Полевые(экспериментальные) данные, отсортированы по AB/2 затем по MN/2
     */
    @get:JsonGetter("experimentalData")
    val sortedExperimentalData: List<ExperimentalData> by lazy { experimentalData.sortedWith(orderByDistances()) }

    /**
     * Без отключенных и если одинаковые AB/2, то с наибольшим MN/2
     */
    @get:JsonIgnore
    val effectiveExperimentalData: List<ExperimentalData> by lazy {
        sortedExperimentalData
            .filter { !it.isHidden }
            .asReversed()
            .distinctBy { it.ab2 } // Берет *первый* элемент из всех равных, т.к. сортировка была по MN/2, можно просто развернуть список
            .asReversed()
    }
}
