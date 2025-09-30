package ru.nucodelabs.geo.ves

import ru.nucodelabs.geo.ves.calc.orderByDistances


interface ExperimentalDataSet {
    val sortedExperimentalData: List<ReadOnlyExperimentalSignal>
    val effectiveExperimentalData: List<ReadOnlyExperimentalSignal>
    val offsetX: Double
}

interface MutableExperimentalDataSet<E : ReadOnlyExperimentalSignal> : ExperimentalDataSet {
    fun addSignal(signal: E)
    fun addSignals(signals: Iterable<E>)
    fun removeSignal(signal: E)
    fun removeSignals(signals: Iterable<E>)
    fun edit(index: Int, mutate: MutableExperimentalSignal.() -> Unit)
}

fun toSortedExperimentalData(experimentalData: List<ExperimentalData>): List<ExperimentalData> {
    val acc = mutableListOf<ExperimentalData>()

    // Группируем по AB
    val dupGroups = experimentalData.groupBy { it.ab2 }
    for ((_, group) in dupGroups) {
        // Если больше одного не отключенного дубликата в группе
        acc += if (group.filter { !it.isHidden }.size > 1) {
            val sortedGroup = group.sortedWith(orderByDistances())
            List(sortedGroup.size) { idx ->
                // Отключаем все кроме последнего (с наиб. MN)
                if (idx < sortedGroup.lastIndex) {
                    sortedGroup[idx].copy(isHidden = true)
                } else {
                    sortedGroup[idx].copy(isHidden = false)
                }
            }
        } else {
            group
        }
    }

    return acc.sortedWith(orderByDistances())
}

fun hideExtraSignalsInPlace(sortedExperimentalData: List<MutableExperimentalSignal>) {
    val groupsByAb = sortedExperimentalData.groupBy { it.ab2 }

    for ((_, group) in groupsByAb) {
        if (group.filter { !it.isHidden }.size <= 1) {
            continue
        } else {
            group.sortedWith(orderByDistances()).forEachIndexed { idx, item ->
                item.isHidden = idx != group.lastIndex
            }
        }
    }
}