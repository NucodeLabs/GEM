package ru.nucodelabs.geo.ves

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import ru.nucodelabs.geo.ves.calc.orderByDistances
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
    private var experimentalData: List<@Valid ExperimentalData> = emptyList(),
    @field:Size(max = 40) var modelData: List<@Valid ModelLayer> = emptyList(),
    @field:Min(0) val offsetX: Double = 100.0,
    val z: Double = 0.0,
    val comment: String = ""
) {
    init {
        modelData = modelData.toMutableList().also {
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

    /**
     * Полевые(экспериментальные) данные, отсортированы по AB/2 затем по MN/2
     */
    @get:JsonGetter("experimentalData")
    val sortedExperimentalData: List<ExperimentalData> by lazy {

        val acc = mutableListOf<ExperimentalData>()

        // Группируем по AB
        val dupGroups = experimentalData.groupBy { it.ab2 }
        for ((_, group) in dupGroups) {
            // Если больше одного не отключенного дубликата в группе
            acc += if (group.filter { !it.isHidden }.size > 1) {
                val sortedGroup = group.sortedWith(orderByDistances())
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

        acc.sortedWith(orderByDistances())
    }

    init {
        experimentalData = sortedExperimentalData
    }

    /**
     * Без отключенных и если одинаковые AB/2, то с наибольшим MN/2
     */
    @get:JsonIgnore
    val effectiveExperimentalData: List<ExperimentalData> by lazy {
        sortedExperimentalData.filter { !it.isHidden }
    }
}
