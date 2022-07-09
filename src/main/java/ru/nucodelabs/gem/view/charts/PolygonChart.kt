package ru.nucodelabs.gem.view.charts

import javafx.beans.NamedArg
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.ScatterChart
import javafx.scene.shape.Polygon

/**
 * Draws polygons using points of each series.
 * To get polygons references use `seriesPolygons` map view.
 */
class PolygonChart(
    @NamedArg("xAxis") xAxis: NumberAxis,
    @NamedArg("yAxis") yAxis: NumberAxis
) : ScatterChart<Number, Number>(xAxis, yAxis) {

    init {
        animated = false
    }

    private val _seriesPolygons: MutableMap<Series<Number, Number>, Polygon> = mutableMapOf()
    val seriesPolygons: Map<Series<Number, Number>, Polygon>
        get() = _seriesPolygons

    override fun seriesAdded(series: Series<Number, Number>?, seriesIndex: Int) {
        super.seriesAdded(series, seriesIndex)
        setupPolygons()
    }

    override fun seriesRemoved(series: Series<Number, Number>?) {
        super.seriesRemoved(series)
        plotChildren -= _seriesPolygons[series]
        _seriesPolygons.keys.remove(series)
    }

    override fun layoutPlotChildren() {
        super.layoutPlotChildren()
        setupPolygons()
    }

    private fun setupPolygons() {
        _seriesPolygons.keys.retainAll(data)

        for (series in data) {
            val polygonPointsArr = series.data.flatMap {
                listOf(
                    xAxis.getDisplayPosition(it.xValue.toDouble()),
                    yAxis.getDisplayPosition(it.yValue.toDouble())
                )
            }.toDoubleArray()

            _seriesPolygons.getOrPut(series) {
                Polygon(*polygonPointsArr).also { plotChildren += it }
            }.points.setAll(polygonPointsArr.toList())
        }
    }
}