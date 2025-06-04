package ru.nucodelabs.geo.ves.calc.primarymodel

import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.ModelLayer

class PrimaryModel(private val experimentalData: List<ExperimentalData>) {
    fun get3LayersPrimaryModel(): List<ModelLayer> {
        if (experimentalData.size <= 3) {
            throw IllegalStateException("Для построения стартовой модели требуется ≥ 4 измерений, было ${experimentalData.size}")
        }
        val logExperimentalData = experimentalData.map { ru.nucodelabs.geo.ves.JavaApi.copy(it).ab2(kotlin.math.log(it.ab2)).build() }
        val pointsCnt = logExperimentalData.size
        val ab2min = logExperimentalData[0].ab2
        val ab2max = logExperimentalData[pointsCnt - 1].ab2
        val ab2range = ab2max - ab2min
        val logSplitData = listOf(
            logExperimentalData.filter { it.ab2 <= ab2min + ab2range / 3.0 },
            logExperimentalData.filter { it.ab2 <= ab2min + ab2range * 2.0 / 3.0 && it.ab2 > ab2min + ab2range / 3.0 },
            logExperimentalData.filter { it.ab2 > ab2min + ab2range * 2.0 / 3.0 }
        )
        val modelLayers = mutableListOf<ModelLayer>()
        for (i in logSplitData.indices) {
            if (logSplitData[i].isEmpty()) return emptyList()
            val list = logSplitData[i]
            val avg = list.map { it.resistanceApparent }.average()
            val prevLast = if (i > 0) kotlin.math.exp(logSplitData[i - 1].last().ab2) else 0.0
            modelLayers.add(ModelLayer(kotlin.math.exp(list.last().ab2) - prevLast, avg, false, false))
        }
        modelLayers[modelLayers.size - 1] = ru.nucodelabs.geo.ves.JavaApi.copy(modelLayers.last()).power(0.0).build()
        return modelLayers
    }
}
