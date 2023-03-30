package ru.nucodelabs.gem.view.controller.anisotropy.main.map

import javafx.collections.ObservableList
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import ru.nucodelabs.gem.fxmodel.anisotropy.ObservableAzimuthSignals
import ru.nucodelabs.geo.anisotropy.calc.map.not
import ru.nucodelabs.geo.anisotropy.calc.map.xFromCenter
import ru.nucodelabs.geo.anisotropy.calc.map.yFromCenter
import ru.nucodelabs.kfx.ext.toObservableList

fun toPoints(azimuthSignals: ObservableList<ObservableAzimuthSignals>): ObservableList<Series<Number, Number>> {
    val seriesList =
        azimuthSignals.map { observableAzimuthSignals ->
            observableAzimuthSignals.signals.sortedSignals.map { signal ->
                listOf(
                    XYChart.Data<Number, Number>(
                        xFromCenter(signal.ab2, observableAzimuthSignals.azimuth),
                        yFromCenter(signal.ab2, observableAzimuthSignals.azimuth),
                        signal.resistanceApparent
                    ),
                    XYChart.Data<Number, Number>(
                        xFromCenter(signal.ab2, !observableAzimuthSignals.azimuth),
                        yFromCenter(signal.ab2, !observableAzimuthSignals.azimuth),
                        signal.resistanceApparent
                    )
                )
            }.flatMap { it.toList() }
        }.map {
            Series(it.toObservableList())
        }

    return seriesList.toObservableList()
}