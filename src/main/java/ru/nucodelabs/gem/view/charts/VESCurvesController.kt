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
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import ru.nucodelabs.algorithms.charts.VesCurvesConverter
import ru.nucodelabs.algorithms.charts.x
import ru.nucodelabs.algorithms.charts.y
import ru.nucodelabs.data.ves.Picket
import ru.nucodelabs.data.ves.Section
import ru.nucodelabs.gem.app.model.SectionManager
import ru.nucodelabs.gem.app.snapshot.HistoryManager
import ru.nucodelabs.gem.extensions.fx.get
import ru.nucodelabs.gem.extensions.fx.toObservableList
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.AlertsFactory
import java.net.URL
import java.util.*
import javax.inject.Inject
import kotlin.math.log10

class VESCurvesController @Inject constructor(
    private val picketObservable: ObservableObjectValue<Picket>,
    @Named("VESCurves") private val dataProperty: ObjectProperty<ObservableList<Series<Double, Double>>>,
    private val alertsFactory: AlertsFactory,
    private val sectionManager: SectionManager,
    private val historyManager: HistoryManager<Section>,
    private val vesCurvesConverter: VesCurvesConverter
) : AbstractController() {

    @FXML
    private lateinit var lineChart: LineChart<Double, Double>

    @FXML
    private lateinit var lineChartXAxis: NumberAxis

    @FXML
    private lateinit var lineChartYAxis: NumberAxis

    override val stage: Stage
        get() = lineChart.scene.window as Stage

    private val picket: Picket
        get() = picketObservable.get()!!

    private lateinit var uiProperties: ResourceBundle
    private lateinit var modelCurveDragger: ModelCurveDragger
    private var isDragging = false

    override fun initialize(location: URL, resources: ResourceBundle) {
        picketObservable.addListener { _, _, newValue: Picket? ->
            if (isDragging) {
                updateTheoreticalCurve()
            } else {
                if (newValue != null) {
                    update()
                }
            }
        }

        lineChart.dataProperty().bind(dataProperty)
        uiProperties = resources
        modelCurveDragger = ModelCurveDragger(
            { pointInScene ->
                XYChart.Data(
                    lineChartXAxis.getValueForDisplay(
                        lineChartXAxis.sceneToLocal(pointInScene).x
                    ) as Double,
                    lineChartYAxis.getValueForDisplay(
                        lineChartYAxis.sceneToLocal(pointInScene).y
                    ) as Double
                )
            },
            dataProperty,
            MOD_CURVE_SERIES_INDEX,
            1.0
        )
    }

    private fun update() {
        lineChart.animated = false
        lineChartYAxis.isAutoRanging = true
        updateExpCurves()
        updateTheoreticalCurve()
        updateModelCurve()
    }

    private fun updateTheoreticalCurve() {
        val theorCurveSeries = Series<Double, Double>()
        try {
            theorCurveSeries.data.addAll(
                vesCurvesConverter.theoreticalCurveOf(picket.experimentalData, picket.modelData).map {
                    XYChart.Data(log10(it.x), log10(it.y))
                }
            )
        } catch (e: UnsatisfiedLinkError) {
            alertsFactory.unsatisfiedLinkErrorAlert(e, stage)
        }
        theorCurveSeries.name = uiProperties["theorCurve"]
        dataProperty.get()[THEOR_CURVE_SERIES_INDEX] = theorCurveSeries
    }

    private fun updateModelCurve() {
        val modelCurveSeries = Series<Double, Double>()
        modelCurveSeries.data.addAll(
            vesCurvesConverter.modelCurveOf(picket.modelData).map {
                XYChart.Data(log10(it.x), log10(it.y))
            }
        )
        modelCurveSeries.name = uiProperties["modCurve"]
        dataProperty.get()[MOD_CURVE_SERIES_INDEX] = modelCurveSeries
        addDraggingToModelCurveSeries(modelCurveSeries)
    }

    private fun addDraggingToModelCurveSeries(modelCurveSeries: Series<Double, Double>) {
        modelCurveSeries.node.cursor = Cursor.HAND
        modelCurveSeries.node.onMousePressed = EventHandler { e: MouseEvent ->
            modelCurveSeries.node.requestFocus()
            isDragging = true
            lineChart.animated = false
            lineChartYAxis.isAutoRanging = false
            modelCurveDragger.detectPoints(e)
            modelCurveDragger.setStyle()
        }
        modelCurveSeries.node.onMouseDragged = EventHandler { e: MouseEvent ->
            isDragging = true
            sectionManager.update(
                picket.withModelData(
                    modelCurveDragger.dragHandler(e, picket.modelData)
                )
            )
        }
        modelCurveSeries.node.onMouseReleased = EventHandler {
            historyManager.snapshot()
            modelCurveDragger.resetStyle()
            isDragging = false
            lineChart.animated = true
            lineChartYAxis.isAutoRanging = true
        }
    }

    private fun updateExpCurves() {
        val expCurveSeries = Series(
            vesCurvesConverter.experimentalCurveOf(picket.experimentalData).map {
                XYChart.Data(log10(it.x), log10(it.y))
            }.toObservableList()
        )
        expCurveSeries.name = uiProperties["expCurve"]

        val errUpperExp = Series(
            vesCurvesConverter.experimentalCurveErrorBoundOf(
                picket.experimentalData,
                VesCurvesConverter.BoundType.UPPER_BOUND
            ).map {
                XYChart.Data(log10(it.x), log10(it.y))
            }.toObservableList()
        )
        errUpperExp.name = uiProperties["expCurveUpper"]

        val errLowerExp = Series(
            vesCurvesConverter.experimentalCurveErrorBoundOf(
                picket.experimentalData,
                VesCurvesConverter.BoundType.LOWER_BOUND
            ).map {
                XYChart.Data(log10(it.x), log10(it.y))
            }.toObservableList()
        )
        errLowerExp.name = uiProperties["expCurveLower"]

        dataProperty.get()[EXP_CURVE_SERIES_INDEX] = expCurveSeries
        dataProperty.get()[EXP_CURVE_ERROR_UPPER_SERIES_INDEX] = errUpperExp
        dataProperty.get()[EXP_CURVE_ERROR_LOWER_SERIES_INDEX] = errLowerExp
    }

    fun legendVisibleProperty(): BooleanProperty = lineChart.legendVisibleProperty()

    companion object Order {
        const val EXP_CURVE_SERIES_INDEX = 0
        const val EXP_CURVE_ERROR_UPPER_SERIES_INDEX = 1
        const val EXP_CURVE_ERROR_LOWER_SERIES_INDEX = 2
        const val THEOR_CURVE_SERIES_INDEX = 3
        const val MOD_CURVE_SERIES_INDEX = 4
    }
}