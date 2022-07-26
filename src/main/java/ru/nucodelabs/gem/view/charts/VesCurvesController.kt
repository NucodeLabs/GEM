package ru.nucodelabs.gem.view.charts

import com.google.inject.name.Named
import javafx.beans.property.BooleanProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ObservableObjectValue
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.chart.LineChart
import javafx.scene.chart.XYChart.Data
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.stage.Stage
import javafx.util.StringConverter
import ru.nucodelabs.algorithms.charts.VesCurvesContext
import ru.nucodelabs.algorithms.charts.vesCurvesContext
import ru.nucodelabs.algorithms.forward_solver.ForwardSolver
import ru.nucodelabs.data.fx.ObservableSection
import ru.nucodelabs.data.ves.*
import ru.nucodelabs.gem.app.snapshot.HistoryManager
import ru.nucodelabs.gem.extensions.fx.forCharts
import ru.nucodelabs.gem.extensions.fx.get
import ru.nucodelabs.gem.extensions.fx.toObservableList
import ru.nucodelabs.gem.extensions.std.exp10
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.gem.view.control.chart.applyLegendStyleAccordingToSeries
import ru.nucodelabs.gem.view.control.chart.installTooltips
import ru.nucodelabs.gem.view.control.chart.length
import ru.nucodelabs.gem.view.control.chart.log.LogarithmicAxis
import ru.nucodelabs.gem.view.control.chart.log.LogarithmicChartNavigationSupport
import java.lang.Double.max
import java.lang.Double.min
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.log10

private const val X_AXIS_PADDING_LOG = 0.1
private const val Y_AXIS_PADDING_LOG = 0.1
private const val X_MIN_LOG = -2.0
private const val X_MIN = 1e-2
private const val X_MAX_LOG = 10.0
private const val X_MAX = 1e10
private const val Y_MIN_LOG = -2.0
private const val Y_MIN = 1e-2
private const val Y_MAX_LOG = 10.0
private const val Y_MAX = 1e10
private const val ZOOM_DELTA_LOG = 0.1
private const val ZOOM_DELTA_REL = 0.1

class VesCurvesController @Inject constructor(
    private val picketObservable: ObservableObjectValue<Picket>,
    @Named("VESCurves") private val dataProperty: ObjectProperty<ObservableList<Series<Number, Number>>>,
    private val _picketIndex: IntegerProperty,
    private val alertsFactory: AlertsFactory,
    private val observableSection: ObservableSection,
    private val historyManager: HistoryManager<Section>,
    private val decimalFormat: DecimalFormat,
    private val formatter: StringConverter<Number>,
    private val forwardSolver: ForwardSolver
) : AbstractController() {

    // SERIES CSS STYLE CLASSES
    private val modelLineStyle = "model-line"
    private val theorLineStyle = "theor-line"
    private val transparentLineStyle = "transparent-line"

    // SYMBOL CSS STYLE CLASSES
    private val modelSymbolStyle = "model-symbol"
    private val theorSymbolStyle = "theor-symbol"
    private val expSymbolStyle = "exp-symbol"
    private val expUpperSymbolStyle = "exp-upper-symbol"
    private val expLowerSymbolStyle = "exp-lower-symbol"
    private val hiddenSymbolStyle = "hidden-symbol"


    private val picketIndex
        get() = _picketIndex.get()

    @FXML
    lateinit var title: Label

    @FXML
    private lateinit var lineChart: LineChart<Number, Number>

    @FXML
    lateinit var xAxis: LogarithmicAxis

    @FXML
    private lateinit var yAxis: LogarithmicAxis

    override val stage: Stage
        get() = lineChart.scene.window as Stage

    private val picket
        get() = picketObservable.get()!!

    private lateinit var vesCurvesContext: VesCurvesContext
    private var effectiveToSortedMapping = intArrayOf()

    private lateinit var uiProperties: ResourceBundle
    private lateinit var modelCurveDragger: ModelCurveDragger
    private var isDraggingModel = false

    private lateinit var zoom: LogarithmicChartNavigationSupport
    private lateinit var zoomCoords: Pair<Double, Double>


    private fun xAxisRangeLog() = log10(xAxis.upperBound / xAxis.lowerBound)

    private fun yAxisRangeLog() = log10(yAxis.upperBound / yAxis.lowerBound)

    override fun initialize(location: URL, resources: ResourceBundle) {
        zoom = LogarithmicChartNavigationSupport(xAxis, yAxis)

        xAxis.tickLabelFormatter = formatter
        yAxis.tickLabelFormatter = formatter

        lineChart.installTooltips(::tooltipFactory)

        picketObservable.addListener { _, _, newValue: Picket? ->
            if (newValue != null) {
                vesCurvesContext = picket.vesCurvesContext
                if (isDraggingModel) {
                    updateTheoreticalCurve()
                    applyStyle()
                } else {
                    update()
                    applyStyle()
                }
            }
        }

        lineChart.dataProperty().bind(dataProperty)
        uiProperties = resources
        modelCurveDragger = ModelCurveDragger(
            xAxis = xAxis,
            yAxis = yAxis,
            vesCurvesData = dataProperty,
            modelCurveIndex = MOD_CURVE_SERIES_INDEX,
            lowerLimitY = 1.0
        )
    }

    private fun update() {
        mapIndices()
        updateExpCurves()
        updateTheoreticalCurve()
        updateModelCurve()
        applyStyle()

        setupXAxisBounds()
        setupYAxisBounds()
    }

    @FXML
    private fun zoom(e: ScrollEvent) {
        val position: Pair<Double, Double> = Pair(
            lineChart.xAxis.sceneToLocal(Point2D(e.sceneX, e.sceneY)).x/lineChart.xAxis.length,
            lineChart.yAxis.sceneToLocal(Point2D(e.sceneX, e.sceneY)).y/lineChart.yAxis.length
        )
        val dY = e.deltaY
        val scale = 1.0 + dY / lineChart.yAxis.length
        zoom.zoom(scale, position)
    }

    @FXML
    private fun pressed(e: MouseEvent) {
        if (isDraggingModel)
            return
        zoomCoords = Pair(e.sceneX, e.sceneY)
    }

    @FXML
    private fun drugged(e: MouseEvent) {
        if (isDraggingModel)
            return
        val dX = e.sceneX - zoomCoords.first
        val dY = e.sceneY - zoomCoords.second

        zoomCoords = Pair(e.sceneX, e.sceneY)
        val deltaCoords = Pair(dX / lineChart.xAxis.length * -1.0, dY / lineChart.yAxis.length)
        zoom.drag(deltaCoords)
    }

    private fun mapIndices() {
        effectiveToSortedMapping = picket.effectiveToSortedIndicesMapping()
    }

    private fun paddingLowerBound(bound: Double, range: Double, padding: Double): Double =
        bound - bound / exp10(range * padding)

    private fun paddingUpperBound(bound: Double, range: Double, padding: Double) =
        bound * exp10(range * padding) - bound

    private fun setupXAxisBounds() {
        val xMin: Double
        val xMax: Double
        if (picket.modelData.isNotEmpty()
            || picket.sortedExperimentalData.isNotEmpty()
        ) {
            xMin = min(
                picket.modelData.firstOrNull()?.power ?: Double.MAX_VALUE,
                picket.sortedExperimentalData.firstOrNull()?.ab2 ?: Double.MAX_VALUE
            )
            xMax = max(
                picket.z - (picket.zOfModelLayers().getOrElse(picket.modelData.lastIndex - 1) { 1e4 }),
                picket.sortedExperimentalData.lastOrNull()?.ab2 ?: Double.MIN_VALUE
            )
        } else if (picket.modelData.size == 1) {
            xMin = picket.sortedExperimentalData.firstOrNull()?.ab2 ?: 1.0
            xMax = picket.sortedExperimentalData.lastOrNull()?.ab2 ?: 100.0
        } else {
            xMin = 1.0
            xMax = 1000.0
        }

        val range = xAxisRangeLog()
        xAxis.lowerBound =
            (xMin - paddingLowerBound(xMin, range, X_AXIS_PADDING_LOG)).coerceAtLeast(1e-3)
        xAxis.upperBound =
            (xMax + paddingUpperBound(xMax, range, X_AXIS_PADDING_LOG))
    }

    private fun setupYAxisBounds() {
        if (picket.modelData.isNotEmpty()
            || picket.sortedExperimentalData.isNotEmpty()
        ) {
            yAxis.lowerBound = min(
                picket.modelData.minOfOrNull { it.resistance } ?: Double.MAX_VALUE,
                picket.sortedExperimentalData.minOfOrNull { it.resistanceApparent } ?: Double.MAX_VALUE
            )
            yAxis.upperBound = max(
                picket.modelData.maxOfOrNull { it.resistance } ?: Double.MIN_VALUE,
                picket.sortedExperimentalData.maxOfOrNull { it.resistanceApparent } ?: Double.MIN_VALUE
            )
        } else {
            yAxis.lowerBound = 1.0
            yAxis.upperBound = 1000.0
        }

        val range = yAxisRangeLog()
        yAxis.lowerBound -= paddingLowerBound(yAxis.lowerBound, range, Y_AXIS_PADDING_LOG)
        yAxis.upperBound += paddingUpperBound(yAxis.upperBound, range, Y_AXIS_PADDING_LOG)
    }

    private fun updateTheoreticalCurve() {
        val theorCurveSeries = Series<Number, Number>()
        try {
            theorCurveSeries.data.addAll(
                vesCurvesContext.theoreticalCurveBy(forwardSolver).map { (x, y) -> Data(x as Number, y as Number) }
            )
        } catch (e: UnsatisfiedLinkError) {
            alertsFactory.unsatisfiedLinkErrorAlert(e, stage)
        }
        theorCurveSeries.name = uiProperties["theorCurve"]
        dataProperty.get()[THEOR_CURVE_SERIES_INDEX] = theorCurveSeries
    }

    private fun updateModelCurve() {
        val modelCurveSeries = Series<Number, Number>()
        modelCurveSeries.data.addAll(
            vesCurvesContext.modelStepGraph().map { (x, y) -> Data(x as Number, y as Number) }
        )
        modelCurveSeries.name = uiProperties["modCurve"]
        dataProperty.get()[MOD_CURVE_SERIES_INDEX] = modelCurveSeries

        addDraggingToModelCurveSeries(modelCurveSeries)
    }

    private fun addDraggingToModelCurveSeries(modelCurveSeries: Series<*, *>) {
        modelCurveSeries.data.forEach { it.node.cursor = Cursor.HAND }
        modelCurveSeries.node.cursor = Cursor.HAND
        modelCurveSeries.node.onMousePressed = EventHandler { e: MouseEvent ->
            modelCurveSeries.node.requestFocus()
            isDraggingModel = true
//            lineChart.animated = false
//            lineChartYAxis.isAutoRanging = false
            modelCurveDragger.detectPoints(e)
            modelCurveDragger.setupStyle()
        }
        modelCurveSeries.node.onMouseDragged = EventHandler { e: MouseEvent ->
            isDraggingModel = true
            observableSection.pickets[picketIndex] =
                picket.copy(modelData = modelCurveDragger.handleMouseDragged(e, picket.modelData.toMutableList()))
        }
        modelCurveSeries.node.onMouseReleased = EventHandler {
            historyManager.snapshot()
            modelCurveDragger.resetStyle()
            isDraggingModel = false
            update()
//            lineChart.animated = true
//            lineChartYAxis.isAutoRanging = true
        }
    }

    private fun updateExpCurves() {
        val expCurveSeries = Series(
            vesCurvesContext.experimentalCurve.map { (x, y) -> Data(x as Number, y as Number) }.toObservableList()
        )
        expCurveSeries.name = uiProperties["expCurve"]

        val errUpperExp = Series(
            vesCurvesContext.experimentalCurveErrorUpperBound.map { (x, y) ->
                Data(x as Number, y as Number)
            }.toObservableList()
        )
        errUpperExp.name = uiProperties["expCurveUpper"]

        val errLowerExp = Series(
            vesCurvesContext.experimentalCurveErrorLowerBound.map { (x, y) ->
                Data(x as Number, y as Number)
            }.toObservableList()
        )
        errLowerExp.name = uiProperties["expCurveLower"]

        val hiddenPoints = Series(
            vesCurvesContext.experimentalHiddenPoints.map { (x, y) ->
                Data(x as Number, y as Number)
            }.toObservableList()
        )
        hiddenPoints.name = uiProperties["hiddenPoints"]

        dataProperty.get()[HIDDEN_SERIES_INDEX] = hiddenPoints
        dataProperty.get()[EXP_CURVE_ERROR_UPPER_SERIES_INDEX] = errUpperExp
        dataProperty.get()[EXP_CURVE_ERROR_LOWER_SERIES_INDEX] = errLowerExp
        dataProperty.get()[EXP_CURVE_SERIES_INDEX] = expCurveSeries
    }

    fun legendVisibleProperty(): BooleanProperty = lineChart.legendVisibleProperty()

    private fun tooltipFactory(
        seriesIndex: Int,
        series: Series<Number, Number>,
        pointIndex: Int,
        point: Data<Number, Number>
    ): Tooltip? {
        return when (seriesIndex) {
            EXP_CURVE_SERIES_INDEX, EXP_CURVE_ERROR_LOWER_SERIES_INDEX, EXP_CURVE_ERROR_UPPER_SERIES_INDEX -> {
                val x = decimalFormat.format(point.xValue)
                val yLower = decimalFormat.format(
                    picket.effectiveExperimentalData[pointIndex].resistanceApparentLowerBoundByError
                )
                val yUpper = decimalFormat.format(
                    picket.effectiveExperimentalData[pointIndex].resistanceApparentUpperBoundByError
                )
                val y = decimalFormat.format(picket.effectiveExperimentalData[pointIndex].resistanceApparent)
                Tooltip(
                    """
                    №${effectiveToSortedMapping[pointIndex] + 1}
                    AB/2 = $x m
                    ρₐ = $y Ω‧m
                    min ρₐ = $yLower
                    max ρₐ = $yUpper
                """.trimIndent()
                ).forCharts()
            }
            else -> null
        }
    }

    private fun applyStyle() {
        fun Series<*, *>.lineStyle(style: String) {
            node.lookup(".chart-series-line").styleClass += style
        }
        lineChart.data[EXP_CURVE_SERIES_INDEX].lineStyle(transparentLineStyle)
        lineChart.data[EXP_CURVE_ERROR_UPPER_SERIES_INDEX].lineStyle(transparentLineStyle)
        lineChart.data[EXP_CURVE_ERROR_LOWER_SERIES_INDEX].lineStyle(transparentLineStyle)
        lineChart.data[HIDDEN_SERIES_INDEX].lineStyle(transparentLineStyle)
        lineChart.data[THEOR_CURVE_SERIES_INDEX].lineStyle(theorLineStyle)
        lineChart.data[MOD_CURVE_SERIES_INDEX].lineStyle(modelLineStyle)

        fun Series<*, *>.symbolStyle(style: String) {
            data.forEach { it.node.lookup(".chart-line-symbol").styleClass += style }
        }
        lineChart.data[EXP_CURVE_SERIES_INDEX].symbolStyle(expSymbolStyle)
        lineChart.data[EXP_CURVE_ERROR_UPPER_SERIES_INDEX].symbolStyle(expUpperSymbolStyle)
        lineChart.data[EXP_CURVE_ERROR_LOWER_SERIES_INDEX].symbolStyle(expLowerSymbolStyle)
        lineChart.data[HIDDEN_SERIES_INDEX].symbolStyle(hiddenSymbolStyle)
        lineChart.data[THEOR_CURVE_SERIES_INDEX].symbolStyle(theorSymbolStyle)
        lineChart.data[MOD_CURVE_SERIES_INDEX].symbolStyle(modelSymbolStyle)

        lineChart.applyLegendStyleAccordingToSeries()
    }

//    @FXML
//    private fun chartOnMouseDragged(mouseEvent: MouseEvent) {
//        if (!isDraggingModel) {
//            dragViewSupport.handleMouseDragged(mouseEvent)
//        }
//    }
//
//    @FXML
//    private fun chartOnMousePressed(mouseEvent: MouseEvent) {
//        if (!isDraggingModel) {
//            dragViewSupport.handleMousePressed(mouseEvent)
//        }
//    }

//    @FXML
//    private fun zoomIn() = zoomSupport.zoomIn(ZOOM_DELTA_LOG)
//
//    @FXML
//    private fun zoomOut() = zoomSupport.zoomOut(ZOOM_DELTA_LOG)

//    fun zoomUsingScroll(scrollEvent: ScrollEvent) {
//        if (scrollEvent.isShortcutDown) {
//            val start = pointInSceneToValue(Point2D(scrollEvent.sceneX, scrollEvent.sceneY))
//            val now = pointInSceneToValue(
//                Point2D(scrollEvent.sceneY + scrollEvent.deltaY, scrollEvent.sceneX + scrollEvent.deltaX)
//            )
//
//            val sensitivity = 0.1
//            val distX = (now.xValue - start.xValue) * sensitivity
//            val distY = (start.xValue - start.yValue) * sensitivity
//
//            lineChartXAxis.lowerBound += distX
//            lineChartXAxis.upperBound -= distX
//
//            lineChartYAxis.lowerBound += distY
//            lineChartYAxis.upperBound -= distY
//        }
//    }


    companion object Order {
        const val EXP_CURVE_SERIES_INDEX = 3
        const val EXP_CURVE_ERROR_UPPER_SERIES_INDEX = 2
        const val EXP_CURVE_ERROR_LOWER_SERIES_INDEX = 1
        const val THEOR_CURVE_SERIES_INDEX = 4
        const val MOD_CURVE_SERIES_INDEX = 5
        const val HIDDEN_SERIES_INDEX = 0

        @JvmField
        val TOTAL_COUNT = maxOf(
            EXP_CURVE_SERIES_INDEX,
            EXP_CURVE_ERROR_LOWER_SERIES_INDEX,
            EXP_CURVE_ERROR_UPPER_SERIES_INDEX,
            THEOR_CURVE_SERIES_INDEX,
            MOD_CURVE_SERIES_INDEX,
            HIDDEN_SERIES_INDEX
        ) + 1
    }
}