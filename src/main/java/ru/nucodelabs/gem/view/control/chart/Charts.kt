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

/**
 * Installs tooltips for each point in each series,
 * then listens to updates and adds tooltips on newly added Points.
 * @param factory takes index of series in data and index of point in series and returns tooltip instance
 */
fun <X, Y> XYChart<X, Y>.installTooltips(factory: (seriesIndex: Int, series: Series<X, Y>, pointIndex: Int, point: Data<X, Y>) -> Tooltip?) {
    for ((sIdx, series) in data.withIndex()) {
        for ((pIdx, point) in series.data.withIndex()) {
            Tooltip.install(point.node, factory(sIdx, series, pIdx, point))
        }
    }

    val dataChangeListener = ListChangeListener<Series<X, Y>> { c ->
        while (c.next()) {
            val added = c.addedSubList
            val sIdxMap = List(added.size) { data.indexOf(added[it]) }
            for ((sIdx, series) in added.withIndex()) {
                for ((pIdx, point) in series.data.withIndex()) {
                    Tooltip.install(point.node, factory(sIdxMap[sIdx], series, pIdx, point))
                }
            }
        }
    }

    fun listenEachSeries() {
        data.forEachIndexed { sIdx, series ->
            series.data.addListener(ListChangeListener { c ->
                while (c.next()) {
                    val added = c.addedSubList
                    val pIdxMap = List(added.size) { series.data.indexOf(added[it]) }
                    for ((pIdx, point) in added.withIndex()) {
                        Tooltip.install(point.node, factory(sIdx, series, pIdxMap[pIdx], point))
                    }
                }
            })
        }
    }

    data.addListener(dataChangeListener)
    listenEachSeries()
    dataProperty().addListener { _, _, newData ->
        newData.addListener(dataChangeListener)
        listenEachSeries()
    }
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