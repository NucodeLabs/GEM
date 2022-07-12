package ru.nucodelabs.gem.view.charts

import javafx.beans.property.ObjectProperty
import javafx.collections.ObservableList
import javafx.geometry.Point2D
import javafx.scene.chart.ValueAxis
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.input.MouseEvent
import ru.nucodelabs.data.ves.ModelLayer
import ru.nucodelabs.gem.view.control.chart.valueForMouseCoordinates
import ru.nucodelabs.gem.view.control.chart.valueForSceneCoordinates
import kotlin.properties.Delegates

/**
 * Enables drag-n-drop functionality on given line chart for step curve (Model Curve)
 */
class ModelCurveDragger
/**
 * Adds drag-n-drop functionality for model step chart.
 * Modifies values of given property and handle ModelData.
 *
 * @param vesCurvesData                 data property of line chart or bound
 * @param modelCurveIndex           index of series in data
 */(
    private val vesCurvesData: ObjectProperty<ObservableList<Series<Number, Number>>>,
    private val modelCurveIndex: Int,
    private val lowerLimitY: Double,
    xAxis: ValueAxis<Number>,
    yAxis: ValueAxis<Number>
) {
    private val axes: Pair<ValueAxis<Number>, ValueAxis<Number>> = xAxis to yAxis

    // mapping: point on chart --> index of the value in data model arrays
    private lateinit var pointResistanceMap: MutableMap<XYChart.Data<*, *>, Int>
    private lateinit var pointPowerMap: MutableMap<XYChart.Data<*, *>, Int>

    // ends of line to be dragged
    private var point1: XYChart.Data<Number, Number>? = null
    private var point2: XYChart.Data<Number, Number>? = null

    // for vertical line dragging
    private var leftLimitX by Delegates.notNull<Double>()
    private var rightLimitX by Delegates.notNull<Double>()

    /**
     * Detects two points that will be dragged by mouse
     *
     * @param mouseEvent mouse pressed event
     */
    fun detectPoints(mouseEvent: MouseEvent) {
        val mouseX = coordinatesToValues(mouseEvent).xValue

        val mouseXLeftBound = coordinatesToValues(
            Point2D(mouseEvent.sceneX - TOLERANCE_ABS, mouseEvent.sceneY)
        ).xValue

        val mouseXRightBound = coordinatesToValues(
            Point2D(mouseEvent.sceneX + TOLERANCE_ABS, mouseEvent.sceneY)
        ).xValue

        val points = vesCurvesData.get()[modelCurveIndex].data

        val closestVerticalLines = points.filter {
            mouseXLeftBound.toDouble() < it.xValue.toDouble() && it.xValue.toDouble() < mouseXRightBound.toDouble()
        }

        if (closestVerticalLines.size == 2) {
            point1 = closestVerticalLines[0]
            point2 = closestVerticalLines[1]
            for (point in points) {
                if (point.xValue.toDouble() < mouseX && point.xValue.toDouble() < point1!!.xValue.toDouble()) {
                    leftLimitX = point.xValue.toDouble()
                }
                if (point.xValue.toDouble() > mouseX && point.xValue.toDouble() > point2!!.xValue.toDouble()) {
                    rightLimitX = point.xValue.toDouble()
                    break
                }
            }
        } else {
            for (point in points) {
                if (point.xValue.toDouble() < mouseX) {
                    point1 = point
                }
                if (point.xValue.toDouble() > mouseX) {
                    point2 = point
                    break
                }
            }
        }
    }

    fun setupStyle() {
        if (point1 != null && point2 != null) {
            val style = """
                    -fx-background-color: blue;
                    """
            point1!!.node.lookup(".chart-line-symbol").style = style
            point2!!.node.lookup(".chart-line-symbol").style = style
        }
    }

    /**
     * Drags the points by mouse and modifies ModelData values and returns same instance
     *
     * @param mouseEvent mouse dragged event
     * @param modelData  model data
     */
    fun handleMouseDragged(mouseEvent: MouseEvent, modelData: MutableList<ModelLayer>): List<ModelLayer> {
        val mutableModelData = modelData.toMutableList()

        mapModelData(mutableModelData)

        val valuesForAxis = coordinatesToValues(mouseEvent)
        val mouseX = valuesForAxis.xValue
        val mouseY = valuesForAxis.yValue

        val mouseXLeftBound = coordinatesToValues(
            Point2D(mouseEvent.sceneX - TOLERANCE_ABS, mouseEvent.sceneY)
        ).xValue
        val mouseXRightBound = coordinatesToValues(
            Point2D(mouseEvent.sceneX + TOLERANCE_ABS, mouseEvent.sceneY)
        ).xValue

        if (point1 != null && point2 != null) {
            // вертикальная линия
            if (point1!!.xValue == point2!!.xValue
                && leftLimitX < mouseXLeftBound && mouseXRightBound < rightLimitX
            ) {
                val index1 = pointPowerMap[point1 as XYChart.Data<*, *>]!!
                val index2 = index1 + 1 // neighbor

                if (!modelData[index1].isFixedPower
                    && !modelData[index2].isFixedPower
                ) {
                    val diff = mouseX - point1!!.xValue.toDouble()

                    point1!!.xValue = mouseX
                    point2!!.xValue = mouseX

                    val initialValue1 = mutableModelData[index1].power
                    val newValue1 = initialValue1 + diff

                    mutableModelData[index1] = mutableModelData[index1].withPower(newValue1)

                    if (index2 < mutableModelData.lastIndex) {
                        val initialValue2 = mutableModelData[index2].power
                        val newValue2 = initialValue2 - diff
                        mutableModelData[index2] = mutableModelData[index2].withPower(newValue2)
                    }
                }
            } else if (point1!!.yValue == point2!!.yValue // горизонтальная линия
                && mouseY > lowerLimitY
            ) {
                val index = pointResistanceMap[point1!!]!!

                if (!modelData[index].isFixedResistance) {
                    point1!!.yValue = mouseY
                    point2!!.yValue = mouseY
                    val newValue = mouseY
                    mutableModelData[index] = mutableModelData[index].withResistance(newValue)
                }
            }
        }
        return mutableModelData
    }

    /**
     * Maps ModelData that will be modified by dragging points
     *
     * @param modelData model data that match curve
     */
    private fun mapModelData(modelData: List<ModelLayer>) {
        val E_MSG = "ModelData array size: %d does not match mapping size: %d"
        pointResistanceMap = HashMap()
        val points = vesCurvesData.get()[modelCurveIndex].data
        run {
            var i = 0
            var j = 0
            while (i < points.size) {
                pointResistanceMap[points[i]] = j
                pointResistanceMap[points[i + 1]] = j
                i += 2
                j++
            }
        }
        require(pointResistanceMap.values.stream().distinct().count() == modelData.size.toLong()) {
            String.format(
                E_MSG,
                pointResistanceMap.values.stream().distinct().count(),
                modelData.size
            )
        }
        pointPowerMap = HashMap()
        var i = 1
        var j = 0
        while (i < points.size - 1) {
            pointPowerMap[points[i]] = j
            pointPowerMap[points[i + 1]] = j
            i += 2
            j++
        }
        require(pointPowerMap.values.stream().distinct().count() == (modelData.size - 1).toLong()) {
            String.format(
                E_MSG,
                pointPowerMap.values.stream().distinct().count(),
                modelData.size
            )
        }
    }

    /**
     * Converts mouse event coordinates to valid values for axis
     *
     * @param mouseEvent mouse pressed/dragged event
     * @return point with valid X and Y values
     */
    private fun coordinatesToValues(mouseEvent: MouseEvent): XYChart.Data<Double, Double> =
        axes.valueForMouseCoordinates(mouseEvent)

    private fun coordinatesToValues(pointInScene: Point2D): XYChart.Data<Double, Double> =
        axes.valueForSceneCoordinates(pointInScene)

    fun resetStyle() {
        if (point1 != null && point2 != null) {
            val style = """
                     -fx-background-color: transparent;
                    """
            point1!!.node.lookup(".chart-line-symbol").style = style
            point2!!.node.lookup(".chart-line-symbol").style = style
        }
    }

    companion object {
        private const val TOLERANCE_ABS = 2.0
    }
}