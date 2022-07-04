package ru.nucodelabs.gem.view.charts

import javafx.beans.property.ObjectProperty
import javafx.beans.value.ObservableObjectValue
import javafx.collections.FXCollections.observableList
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.Label
import javafx.stage.Stage
import ru.nucodelabs.algorithms.charts.*
import ru.nucodelabs.data.ves.Picket
import ru.nucodelabs.gem.extensions.fx.observableListOf
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.AlertsFactory
import java.math.RoundingMode
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.log10

class MisfitStacksController @Inject constructor(
    private val picketObservable: ObservableObjectValue<Picket>,
    private val vesCurvesConverter: VesCurvesConverter,
    private val alertsFactory: AlertsFactory,
    private val dataProperty: ObjectProperty<ObservableList<Series<Double, Double>>>,
    private val misfitValuesFactory: MisfitValuesFactory
) : AbstractController() {
    @FXML
    private lateinit var text: Label

    @FXML
    private lateinit var lineChart: LineChart<Double, Double>

    @FXML
    private lateinit var lineChartXAxis: NumberAxis

    @FXML
    private lateinit var lineChartYAxis: NumberAxis

    override val stage: Stage
        get() = lineChartXAxis.scene.window as Stage

    private val picket: Picket
        get() = picketObservable.get()!!

    override fun initialize(location: URL, resources: ResourceBundle) {
        picketObservable.addListener { _, _, newValue: Picket? ->
            if (newValue != null) {
                update()
            }
        }

        lineChart.dataProperty().bind(dataProperty)
    }

    private fun update() {
        var misfitStacksSeriesList: MutableList<Series<Double, Double>> = ArrayList()
        try {
            val values = misfitValuesFactory(picket.experimentalData, picket.modelData)
            val expPoints = vesCurvesConverter.experimentalCurveOf(picket.experimentalData).map {
                Point(log10(it.x), log10(it.y))
            }

            misfitStacksSeriesList = observableList(mutableListOf())

            if (picket.experimentalData.isNotEmpty() && picket.modelData.isNotEmpty()
            ) {
                check(values.size == expPoints.size)
                for ((index, expPoint) in expPoints.withIndex()) {
                    misfitStacksSeriesList.add(
                        Series(
                            observableListOf(
                                    XYChart.Data(expPoint.x, 0.0),
                                    XYChart.Data(expPoint.x, values[index])
                            )
                        )
                    )
                }
            }
            val avg = values.map { abs(it) }.average()
            val decimalFormat = DecimalFormat("#.##").apply {
                roundingMode = RoundingMode.HALF_UP
            }
            text.text = "avg = ${decimalFormat.format(avg)} %"
        } catch (e: UnsatisfiedLinkError) {
            alertsFactory.unsatisfiedLinkErrorAlert(e, stage).show()
        } catch (e: IllegalStateException) {
            alertsFactory.simpleExceptionAlert(e, stage).show()
        }
        dataProperty.get().clear()
        dataProperty.get().addAll(misfitStacksSeriesList)
        colorizeMisfitStacksSeries()
    }

    private fun colorizeMisfitStacksSeries() {
        val data = dataProperty.get()
        for (series in data) {
            val nonZeroPoint = series.data[1]
            if (abs(nonZeroPoint.yValue) < 100.0) {
                series.node.style = "-fx-stroke: LimeGreen;"
                nonZeroPoint.node.lookup(".chart-line-symbol").style = "-fx-background-color: LimeGreen"
                val zeroPoint = series.data[0]
                zeroPoint.node.lookup(".chart-line-symbol").style = "-fx-background-color: LimeGreen"
            }
        }
    }
}