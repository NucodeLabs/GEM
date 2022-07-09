package ru.nucodelabs.gem.view.charts

import javafx.scene.chart.NumberAxis
import javafx.scene.chart.ScatterChart
import javafx.scene.shape.Polygon

/**
 * Draws polygons using points of each series.
 * To get polygons references use `seriesPolygons` map view.
 */
class PolygonChart(xAxis: NumberAxis, yAxis: NumberAxis) : ScatterChart<Number, Number>(xAxis, yAxis) {

    init {
        animated = false
    }

    private val _seriesPolygons: MutableMap<Series<Number, Number>, Polygon> = mutableMapOf()
    val seriesPolygons: Map<Series<Number, Number>, Polygon>
        get() = _seriesPolygons

    override fun layoutPlotChildren() {
        super.layoutPlotChildren()

        _seriesPolygons.keys.retainAll(data)

        for (series in data) {
            val polygonPointsArr = series.data.flatMap {
                listOf(
                    xAxis.getDisplayPosition(it.xValue.toDouble()),
                    yAxis.getDisplayPosition(it.yValue.toDouble())
                )
            }.toDoubleArray()

            if (series !in _seriesPolygons) {
                val polygon = Polygon(*polygonPointsArr)
                plotChildren += polygon
                _seriesPolygons[series] = polygon
            } else {
                _seriesPolygons[series]?.points?.setAll(polygonPointsArr.toList())
            }
        }
    }
}