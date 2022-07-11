package ru.nucodelabs.gem.view.charts

import com.google.inject.name.Named
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ObservableObjectValue
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Cursor
import javafx.scene.chart.LineChart
import javafx.scene.chart.XYChart.Data
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.Tooltip
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import javafx.util.Duration
import ru.nucodelabs.algorithms.charts.VesCurvesConverter
import ru.nucodelabs.data.ves.Picket
import ru.nucodelabs.data.ves.Section
import ru.nucodelabs.gem.app.model.SectionManager
import ru.nucodelabs.gem.app.snapshot.HistoryManager
import ru.nucodelabs.gem.extensions.fx.get
import ru.nucodelabs.gem.extensions.fx.toObservableList
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.gem.view.control.chart.*
import java.lang.Double.max
import java.lang.Double.min
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

private const val X_AXIS_LOG_PADDING = 0.2
private const val Y_AXIS_LOG_PADDING = 0.2
private const val X_AXIS_PADDING_REL = 0.1
private const val Y_AXIS_PADDING_REL = 0.1
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
    private val alertsFactory: AlertsFactory,
    private val sectionManager: SectionManager,
    private val historyManager: HistoryManager<Section>,
    private val vesCurvesConverter: VesCurvesConverter,
    private val decimalFormat: DecimalFormat
) : AbstractController() {

    @FXML
    private lateinit var lineChart: LineChart<Number, Number>

    @FXML
    lateinit var lineChartXAxis: LogarithmicAxis

    @FXML
    private lateinit var lineChartYAxis: LogarithmicAxis

    override val stage: Stage
        get() = lineChart.scene.window as Stage

    private val picket: Picket
        get() = picketObservable.get()!!

    private lateinit var uiProperties: ResourceBundle
    private lateinit var modelCurveDragger: ModelCurveDragger
    private var isDraggingModel = false
    private val tooltips = mutableMapOf<Data<*, *>, Tooltip>()

    private lateinit var dragViewSupport: DragViewSupport
    private lateinit var zoomSupport: ZoomSupport

    private val xAxisRange
        get() = lineChartXAxis.upperBound - lineChartXAxis.lowerBound

    private val yAxisRange
        get() = lineChartYAxis.upperBound - lineChartYAxis.lowerBound

    override fun initialize(location: URL, resources: ResourceBundle) {
        picketObservable.addListener { _, _, newValue: Picket? ->
            if (isDraggingModel) {
                updateTheoreticalCurve()
                addTooltips()
            } else {
                if (newValue != null) {
                    update()
                }
            }
        }

        lineChart.dataProperty().bind(dataProperty)
        uiProperties = resources
        modelCurveDragger = ModelCurveDragger(
            xAxis = lineChartXAxis,
            yAxis = lineChartYAxis,
            vesCurvesData = dataProperty,
            modelCurveIndex = MOD_CURVE_SERIES_INDEX,
            lowerLimitY = 1.0
        )

        dragViewSupport = lineChart.initDragViewSupport()

        zoomSupport = lineChart.initZoomSupport()
    }

    private fun update() {
        tooltips.clear()

        updateExpCurves()
        updateTheoreticalCurve()
        updateModelCurve()

        addTooltips()

        setupXAxisBounds()
        setupYAxisBounds()
    }

    private fun setupXAxisBounds() {
        lineChartXAxis.lowerBound = min(
            picket.modelData.firstOrNull()?.power ?: Double.MAX_VALUE,
            picket.experimentalData.firstOrNull()?.ab2 ?: Double.MAX_VALUE
        )

        lineChartXAxis.upperBound = max(
            picket.z - (picket.zOfModelLayers().lastOrNull() ?: picket.z),
            picket.experimentalData.lastOrNull()?.ab2 ?: Double.MIN_VALUE
        )

        val range = xAxisRange
        lineChartXAxis.upperBound += range * X_AXIS_PADDING_REL
        lineChartXAxis.upperBound += range * X_AXIS_PADDING_REL
    }

    private fun setupYAxisBounds() {
        lineChartYAxis.lowerBound = min(
            picket.modelData.minOfOrNull { it.resistance } ?: Double.MAX_VALUE,
            picket.experimentalData.minOfOrNull { it.resistanceApparent } ?: Double.MAX_VALUE
        )

        lineChartYAxis.upperBound = max(
            picket.modelData.maxOfOrNull { it.resistance } ?: Double.MIN_VALUE,
            picket.experimentalData.maxOfOrNull { it.resistanceApparent } ?: Double.MIN_VALUE
        )

        val range = xAxisRange
        lineChartYAxis.upperBound += range * Y_AXIS_PADDING_REL
        lineChartYAxis.upperBound += range * Y_AXIS_PADDING_REL
    }

    // tooltips must be added after nodes shown on screen, otherwise it doesn't work
    private fun addTooltips() {
        for (series in lineChart.data) {
            for (dataPoint in series.data) {
                tooltips[dataPoint]?.let { Tooltip.install(dataPoint.node, it) }
            }
        }
    }

    private fun updateTheoreticalCurve() {
        val theorCurveSeries = Series<Number, Number>()
        try {
            theorCurveSeries.data.addAll(
                vesCurvesConverter.theoreticalCurveOf(picket.experimentalData, picket.modelData)
                    .mapIndexed { i, (x, y) ->
                        Data(x as Number, y as Number).also { tooltips += it to tooltip(i, x, y) }
                    }
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
            vesCurvesConverter.modelCurveOf(picket.modelData).map { (x, y) ->
                Data(x as Number, y as Number).also { tooltips += it to tooltipForModel(x, y) }
            }
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
            sectionManager.update(
                picket.withModelData(
                    modelCurveDragger.handleMouseDragged(e, picket.modelData)
                )
            )
        }
        modelCurveSeries.node.onMouseReleased = EventHandler {
            historyManager.snapshot()
            modelCurveDragger.resetStyle()
            isDraggingModel = false
//            lineChart.animated = true
//            lineChartYAxis.isAutoRanging = true
        }
    }

    private fun updateExpCurves() {
        val expCurveSeries = Series(
            vesCurvesConverter.experimentalCurveOf(picket.experimentalData).mapIndexed { i, (x, y) ->
                Data(x as Number, y as Number).also { tooltips += it to tooltip(i, x, y) }
            }.toObservableList()
        )
        expCurveSeries.name = uiProperties["expCurve"]

        val errUpperExp = Series(
            vesCurvesConverter.experimentalCurveErrorBoundOf(
                picket.experimentalData,
                VesCurvesConverter.BoundType.UPPER_BOUND
            ).mapIndexed { i, (x, y) ->
                Data(x as Number, y as Number).also { tooltips += it to tooltip(i, x, y) }
            }.toObservableList()
        )
        errUpperExp.name = uiProperties["expCurveUpper"]

        val errLowerExp = Series(
            vesCurvesConverter.experimentalCurveErrorBoundOf(
                picket.experimentalData,
                VesCurvesConverter.BoundType.LOWER_BOUND
            ).mapIndexed { i, (x, y) ->
                Data(x as Number, y as Number).also { tooltips += it to tooltip(i, x, y) }
            }.toObservableList()
        )
        errLowerExp.name = uiProperties["expCurveLower"]

        dataProperty.get()[EXP_CURVE_SERIES_INDEX] = expCurveSeries
        dataProperty.get()[EXP_CURVE_ERROR_UPPER_SERIES_INDEX] = errUpperExp
        dataProperty.get()[EXP_CURVE_ERROR_LOWER_SERIES_INDEX] = errLowerExp
    }

    fun legendVisibleProperty(): BooleanProperty = lineChart.legendVisibleProperty()

    private fun tooltip(n: Int, x: Double, y: Double) =
        Tooltip(
            """
               №${n + 1}
               AB/2 = ${decimalFormat.format(x)} m
               ρₐ = ${decimalFormat.format(y)} Ω‧m
            """.trimIndent()
        ).halfSecondDelay()

    private fun tooltipForModel(x: Double, y: Double) =
        Tooltip("Z = ${decimalFormat.format(picket.z - x)} m\nρ = ${decimalFormat.format(y)} Ω‧m")
            .halfSecondDelay()

    private fun Tooltip.halfSecondDelay() = apply { showDelay = Duration.millis(500.0) }

    @FXML
    private fun chartOnMouseDragged(mouseEvent: MouseEvent) {
        if (!isDraggingModel) {
            dragViewSupport.handleMouseDragged(mouseEvent)
        }
    }

    @FXML
    private fun chartOnMousePressed(mouseEvent: MouseEvent) {
        if (!isDraggingModel) {
            dragViewSupport.handleMousePressed(mouseEvent)
        }
    }

    @FXML
    private fun zoomIn() = zoomSupport.zoomIn(ZOOM_DELTA_LOG)

    @FXML
    private fun zoomOut() = zoomSupport.zoomOut(ZOOM_DELTA_LOG)

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
        const val EXP_CURVE_SERIES_INDEX = 0
        const val EXP_CURVE_ERROR_UPPER_SERIES_INDEX = 1
        const val EXP_CURVE_ERROR_LOWER_SERIES_INDEX = 2
        const val THEOR_CURVE_SERIES_INDEX = 3
        const val MOD_CURVE_SERIES_INDEX = 4
    }
}