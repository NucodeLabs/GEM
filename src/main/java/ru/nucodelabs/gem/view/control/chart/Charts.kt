package ru.nucodelabs.gem.view.control.chart

import javafx.collections.ListChangeListener
import javafx.geometry.Point2D
import javafx.scene.chart.ValueAxis
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Data
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.Tooltip
import javafx.scene.input.MouseEvent

/**
 * First = X axis
 * Second = Y Axis
 */
fun Pair<ValueAxis<Number>, ValueAxis<Number>>.valueForMouseCoordinates(mouseEvent: MouseEvent): Data<Double, Double> =
    valueForSceneCoordinates(mouseEvent.sceneX, mouseEvent.sceneY)

/**
 * First = X axis
 * Second = Y Axis
 */
fun Pair<ValueAxis<Number>, ValueAxis<Number>>.valueForSceneCoordinates(
    sceneX: Double,
    sceneY: Double
): Data<Double, Double> {
    val pointInScene = Point2D(sceneX, sceneY)

    return Data(
        first.getValueForDisplay(
            first.sceneToLocal(pointInScene).x
        ) as Double,
        second.getValueForDisplay(
            second.sceneToLocal(pointInScene).y
        ) as Double
    )
}

/**
 * First = X axis
 * Second = Y Axis
 */
fun Pair<ValueAxis<Number>, ValueAxis<Number>>.valueForSceneCoordinates(pointInScene: Point2D) =
    valueForSceneCoordinates(pointInScene.x, pointInScene.y)

fun <X, Y> XYChart<X, Y>.installTooltips(factory: (series: Series<X, Y>, point: Data<X, Y>) -> Tooltip) {
    for (series in data) {
        for (point in series.data) {
            Tooltip.install(point.node, factory(series, point))
        }
    }

    data.addListener(ListChangeListener {
        while (it.next()) {
            for (series in it.addedSubList) {
                for (point in series.data) {
                    Tooltip.install(point.node, factory(series, point))
                }
            }
        }
    })
}