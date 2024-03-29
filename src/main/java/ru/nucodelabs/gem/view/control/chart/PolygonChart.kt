package ru.nucodelabs.gem.view.control.chart

import javafx.beans.NamedArg
import javafx.scene.chart.ScatterChart
import javafx.scene.chart.ValueAxis
import javafx.scene.shape.Polygon

/**
 * Draws polygons using points of each series.
 * To get polygons references use `seriesPolygons` map view.
 */
open class PolygonChart(
    @NamedArg("xAxis") xAxis: ValueAxis<Number>,
    @NamedArg("yAxis") yAxis: ValueAxis<Number>
) : ScatterChart<Number, Number>(xAxis, yAxis) {

    init {
        animated = false
    }

    private val _seriesPolygons: MutableMap<Series<Number, Number>, Polygon> = mutableMapOf()
    val seriesPolygons: Map<Series<Number, Number>, Polygon>
        get() = _seriesPolygons

    override fun seriesAdded(series: Series<Number, Number>, seriesIndex: Int) {
        super.seriesAdded(series, seriesIndex)
        setupPolygon(series)
    }

    override fun seriesRemoved(series: Series<Number, Number>) {
        super.seriesRemoved(series)
        removePolygon(series)
    }

    private fun removePolygon(series: Series<Number, Number>) {
        plotChildren -= _seriesPolygons[series]
        _seriesPolygons.keys -= series
    }

    override fun layoutPlotChildren() {
        super.layoutPlotChildren()
        setupPolygonsAll()
    }

    private fun setupPolygonsAll() {
        for (series in data) {
            setupPolygon(series)
        }
    }

    private fun setupPolygon(series: Series<Number, Number>) {
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