package ru.nucodelabs.gem.view.controller.charts

import javafx.beans.property.ObjectProperty
import javafx.beans.value.ObservableObjectValue
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.stage.Stage
import ru.nucodelabs.gem.util.fx.Point
import ru.nucodelabs.gem.util.fx.forCharts
import ru.nucodelabs.gem.util.fx.line
import ru.nucodelabs.gem.util.fx.observableListOf
import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.gem.view.control.chart.log.LogarithmicAxis
import ru.nucodelabs.gem.view.controller.AbstractController
import ru.nucodelabs.geo.target.TargetFunction
import ru.nucodelabs.geo.target.invoke
import ru.nucodelabs.geo.ves.Picket
import ru.nucodelabs.geo.ves.calc.forward.ForwardSolver
import ru.nucodelabs.geo.ves.calc.graph.MisfitsFunction
import ru.nucodelabs.geo.ves.calc.graph.vesCurvesContext
import java.math.RoundingMode
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.abs

class MisfitStacksController @Inject constructor(
    private val picketObservable: ObservableObjectValue<Picket>,
    private val alertsFactory: AlertsFactory,
    private val dataProperty: ObjectProperty<ObservableList<Series<Number, Number>>>,
    private val misfitsFunction: MisfitsFunction,
    private val decimalFormat: DecimalFormat,
    private val forwardSolver: ForwardSolver,
    private val targetFunction: TargetFunction.WithError
) : AbstractController() {
    @FXML
    private lateinit var text: Label

    @FXML
    private lateinit var lineChart: LineChart<Number, Number>

    @FXML
    lateinit var lineChartXAxis: LogarithmicAxis

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
        var misfitStacksSeriesList: MutableList<Series<Number, Number>> = ArrayList()
        try {
            val misfits = picket.vesCurvesContext.misfitsBy(misfitsFunction)
            val expPoints = picket.vesCurvesContext.experimentalCurve

            misfitStacksSeriesList = observableListOf()

            if (picket.effectiveExperimentalData.isNotEmpty() && picket.modelData.isNotEmpty()) {
                check(misfits.size == expPoints.size)

                for ((index, expPoint) in expPoints.withIndex()) {
                    misfitStacksSeriesList += line(
                        Point(expPoint.x as Number, 0.0 as Number),
                        Point(expPoint.x as Number, misfits[index] as Number)
                    )
                }

                val avg = misfits.map { abs(it) }.average()
                val max = misfits.maxOfOrNull { abs(it) } ?: 0.0

                val theorPoints = picket.vesCurvesContext.theoreticalCurveBy(forwardSolver)
                val misfitsWithoutErr =
                    expPoints.mapIndexed { idx, (_, resApp) -> (resApp - theorPoints[idx].y) / resApp }
                val avgWithoutErr = misfitsWithoutErr.map { abs(it) }.average()
                val maxWithoutErr = misfitsWithoutErr.maxOfOrNull { abs(it) } ?: 0.0
                val dfTwo = DecimalFormat("#.##").apply { roundingMode = RoundingMode.HALF_UP }
                val dfFour = DecimalFormat("#.####").apply { roundingMode = RoundingMode.HALF_UP }
                val targetFunValue = targetFunction(
                    forwardSolver(picket.effectiveExperimentalData, picket.modelData),
                    picket.effectiveExperimentalData.map { it.resistanceApparent },
                    picket.effectiveExperimentalData.map { it.errorResistanceApparent }
                )
                text.text =
                    "целевая функция: f = ${dfFour.format(targetFunValue)}" +
                            "  |  " +
                            "отклонение: avg = ${dfFour.format(avgWithoutErr)}, max = ${
                                dfFour.format(
                                    maxWithoutErr
                                )
                            }" + "  |  " +
                            "погрешность: avg = ${dfTwo.format(avg)}%, max = ${dfTwo.format(max)}%"
            }
        } catch (e: UnsatisfiedLinkError) {
            alertsFactory.unsatisfiedLinkErrorAlert(e, stage).show()
        } catch (e: IllegalStateException) {
            alertsFactory.simpleExceptionAlert(e, stage).show()
        }
        dataProperty.get().clear()
        dataProperty.get() += misfitStacksSeriesList
        colorizeMisfitStacksSeries()
        installTooltips()
    }

    private fun installTooltips() {
        dataProperty.get().forEach {
            val text = "${decimalFormat.format(it.data[1].yValue)}%"
            Tooltip.install(it.node, Tooltip(text).forCharts())
            it.data.forEach { p -> Tooltip.install(p.node, Tooltip(text).forCharts()) }
        }
    }

    private fun colorizeMisfitStacksSeries() {
        val data = dataProperty.get()
        for (series in data) {
            val nonZeroPoint = series.data[1]
            if (abs(nonZeroPoint.yValue.toDouble()) < 100.0) {
                series.node.style = "-fx-stroke: LimeGreen;"
                nonZeroPoint.node.lookup(".chart-line-symbol").style = "-fx-background-color: LimeGreen;"
                val zeroPoint = series.data[0]
                zeroPoint.node.lookup(".chart-line-symbol").style = "-fx-background-color: LimeGreen;"
            }
        }
    }
}