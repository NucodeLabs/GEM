package ru.nucodelabs.gem.view.control.chart

import javafx.beans.NamedArg
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.chart.ValueAxis
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import ru.nucodelabs.gem.util.fx.getValue
import ru.nucodelabs.gem.util.fx.setValue


class PolygonWithNamesChart(
    @NamedArg("xAxis") xAxis: ValueAxis<Number>,
    @NamedArg("yAxis") yAxis: ValueAxis<Number>
) : PolygonChart(xAxis, yAxis) {

    private val namesVisibleProperty = SimpleBooleanProperty(true)
    var namesVisible by namesVisibleProperty
    fun namesVisibleProperty(): BooleanProperty = namesVisibleProperty

    /**
     * Maps series to corresponding Text node
     */
    val seriesText: Map<Series<Number, Number>, Text>
        get() = _seriesText

    private val _seriesText = mutableMapOf<Series<Number, Number>, Text>()

    private fun removeTextGroup(series: Series<Number, Number>) {
        plotChildren -= _seriesText[series]
        _seriesText -= series
    }

    override fun layoutPlotChildren() {
        super.layoutPlotChildren()
        setupTextAll()
    }

    override fun seriesRemoved(series: Series<Number, Number>) {
        super.seriesRemoved(series)
        removeTextGroup(series)
    }

    override fun seriesAdded(series: Series<Number, Number>, seriesIndex: Int) {
        super.seriesAdded(series, seriesIndex)
        setupText(series)
    }

    private fun setupTextAll() {
        for (series in data) {
            setupText(series)
        }
    }

    private fun setupText(series: Series<Number, Number>) {
        val polygon = seriesPolygons[series] ?: return

        val xCoord = polygon.points[0] + 3
        val yCoord = polygon.points[1] + 10

        val text = _seriesText[series]
        if (text != null) {
            text.x = xCoord
            text.y = yCoord
        } else {
            val newText = Text(xCoord, yCoord, series.name ?: "").apply {
                font = Font(11.0)
                fill = Color.WHITE
                effect = DropShadow(2.0, Color.BLACK)
                textProperty().bind(series.nameProperty())
                managedProperty().bind(visibleProperty())
                isVisible = namesVisible
                visibleProperty().bind(namesVisibleProperty)
            }
            plotChildren += newText
            _seriesText[series] = newText
        }
    }
}




