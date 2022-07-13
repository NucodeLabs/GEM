package ru.nucodelabs.gem.view.control.chart

import javafx.beans.binding.DoubleBinding
import javafx.collections.ListChangeListener
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.chart.Axis
import javafx.scene.chart.ValueAxis
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Data
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.text.Font
import javafx.scene.text.Text

fun <T> Axis<T>.getValueForScene(sceneCoordinate: Double): T {
    val local = sceneToLocal(
        if (side.isVertical) Point2D(0.0, sceneCoordinate) else Point2D(sceneCoordinate, 0.0)
    )
    val displayPosition = if (side.isVertical) local.y else local.x
    return getValueForDisplay(displayPosition)
}

fun <T> Axis<T>.getValueForScreen(screenCoordinate: Double): T {
    val local = screenToLocal(
        if (side.isVertical) Point2D(0.0, screenCoordinate) else Point2D(screenCoordinate, 0.0)
    )
    val displayPosition = if (side.isVertical) local.y else local.x
    return getValueForDisplay(displayPosition)
}



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

/**
 * `upperBound - lowerBound`
 */
fun ValueAxis<Number>.rangeBinding(): DoubleBinding = upperBoundProperty().subtract(lowerBoundProperty())

val XYChart<*, *>.plotContentNode: Group
    get() = lookup(".plot-content") as Group

val Axis<*>.labelNode: Label
    get() = childrenUnmodifiable.filterIsInstance<Label>().first()

val Axis<*>.childrenTextNodes: List<Text>
    get() = childrenUnmodifiable.filterIsInstance<Text>()

val <T : Number> ValueAxis<T>.tickMarksTextNodes: Map<Axis.TickMark<T>, Text>
    get() = buildMap {
        val textNodes = childrenTextNodes
        tickMarks.forEach { tick ->
            textNodes.find { node -> node.text == tickLabelFormatter.toString(tick.value) }?.let { node ->
                put(tick, node)
            }
        }
    }

fun Axis<*>.limitTickLabelsWidth(maxWidth: Double, minFontSize: Double = 8.0, maxFontSize: Double = 13.0) {
    val correctFontSize = {
        val nodes = childrenTextNodes
        val maxTickWidth = nodes.maxOfOrNull { it.layoutBounds.width } ?: maxWidth
        val currentFont = tickLabelFont
        tickLabelFont = if (maxTickWidth >= maxWidth) {
            Font.font((currentFont.size - 1).coerceAtLeast(minFontSize))
        } else {
            Font.font((currentFont.size + 1).coerceAtMost(maxFontSize))
        }
    }
    tickMarks.addListener(ListChangeListener { c ->
        if (c.next()) {
            correctFontSize()
        }
    })
    widthProperty().addListener { _, _, _ -> correctFontSize() }
}