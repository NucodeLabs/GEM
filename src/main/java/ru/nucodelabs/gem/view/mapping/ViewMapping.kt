package ru.nucodelabs.gem.view.mapping

import javafx.collections.ObservableList
import javafx.scene.chart.XYChart.Data
import javafx.scene.chart.XYChart.Series
import ru.nucodelabs.gem.fxmodel.anisotropy.ObservableAzimuthSignals
import ru.nucodelabs.gem.fxmodel.anisotropy.ObservableSignal
import ru.nucodelabs.geo.anisotropy.calc.SignalRelation
import ru.nucodelabs.geo.anisotropy.calc.map.not
import ru.nucodelabs.geo.anisotropy.calc.map.xFromCenter
import ru.nucodelabs.geo.anisotropy.calc.map.yFromCenter
import ru.nucodelabs.kfx.ext.toObservableList
import java.text.DecimalFormat

fun mapAzimuthSignals(azimuthSignals: List<ObservableAzimuthSignals>): ObservableList<Series<Number, Number>> {
    val seriesList =
        azimuthSignals.map { observableAzimuthSignals ->
            observableAzimuthSignals.signals.sortedSignals.map { signal ->
                listOf(
                    // TODO забиндить координаты точек к проперти сигналов
                    //  (чтобы не вешать листенеры на их изменение в контроллере)
                    Data<Number, Number>(
                        xFromCenter(signal.ab2, observableAzimuthSignals.azimuth),
                        yFromCenter(signal.ab2, observableAzimuthSignals.azimuth),
                        signal.resistivityApparent
                    ),
                    Data<Number, Number>(
                        xFromCenter(signal.ab2, !observableAzimuthSignals.azimuth),
                        yFromCenter(signal.ab2, !observableAzimuthSignals.azimuth),
                        signal.resistivityApparent
                    )
                )
            }.flatMap { it.toList() }
        }.map {
            Series(it.toObservableList())
        }

    return seriesList.toObservableList()
}

fun mapSignals(signals: List<ObservableSignal>): Series<Number, Number> {
    return Series(
        signals.map {
            Data(it.ab2 as Number, it.resistivityApparent as Number, it).apply {
                XValueProperty().bind(it.ab2Property())
                YValueProperty().bind(it.resistivityApparentProperty())
            }
        }.toObservableList()
    )
}

fun mapSignalsRelations(
    signalRelations: List<SignalRelation>,
    df: DecimalFormat
): ObservableList<Series<Number, Number>> {
    return signalRelations.map { series ->
        Series(
            series.relations.map {
                Data(it.ab2 as Number, it.value as Number)
            }.toObservableList()
        ).apply {
            name = df.format(series.azimuth)
        }
    }.toObservableList()
}