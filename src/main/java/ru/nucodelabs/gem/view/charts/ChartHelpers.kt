package ru.nucodelabs.gem.view.charts

import javafx.geometry.Point2D
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart.Data
import javafx.scene.input.MouseEvent

/**
 * First = X axis
 * Second = Y Axis
 */
fun Pair<NumberAxis, NumberAxis>.valueForMouseCoordinates(mouseEvent: MouseEvent): Data<Double, Double> =
    valueForSceneCoordinates(mouseEvent.sceneX, mouseEvent.sceneY)

/**
 * First = X axis
 * Second = Y Axis
 */
fun Pair<NumberAxis, NumberAxis>.valueForSceneCoordinates(sceneX: Double, sceneY: Double): Data<Double, Double> {
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
fun Pair<NumberAxis, NumberAxis>.valueForSceneCoordinates(pointInScene: Point2D) =
    valueForSceneCoordinates(pointInScene.x, pointInScene.y)