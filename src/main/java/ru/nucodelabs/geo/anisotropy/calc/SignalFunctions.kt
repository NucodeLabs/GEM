@file:JvmName("SignalFunctions")

package ru.nucodelabs.geo.anisotropy.calc

import ru.nucodelabs.geo.anisotropy.Signal

fun orderSignalsByDistances() =
    compareBy<Signal> { it.ab2 }
        .thenBy { it.mn2 }

fun processSignals(signals: List<Signal>): List<Signal> {
    val acc = mutableListOf<Signal>()

    // Группируем по AB
    val dupGroups = signals.groupBy { it.ab2 }
    for ((_, group) in dupGroups) {
        // Если больше одного не отключенного дубликата в группе
        acc += if (group.filter { !it.isHidden }.size > 1) {
            val sortedGroup = group.sortedWith(orderSignalsByDistances())
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

    return acc.sortedWith(orderSignalsByDistances())
}