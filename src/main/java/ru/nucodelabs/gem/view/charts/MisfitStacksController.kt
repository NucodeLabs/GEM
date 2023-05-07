package ru.nucodelabs.gem.view.charts

import javafx.beans.property.ObjectProperty
import javafx.beans.value.ObservableObjectValue
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Stage
import org.jdesktop.swingx.JXLabel.*
import org.jdesktop.swingx.JXLabel.TextAlignment.CENTER
import ru.nucodelabs.gem.util.fx.Point
import ru.nucodelabs.gem.util.fx.forCharts
import ru.nucodelabs.gem.util.fx.line
import ru.nucodelabs.gem.util.fx.observableListOf
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.gem.view.control.chart.log.LogarithmicAxis
import ru.nucodelabs.geo.ves.Picket
import ru.nucodelabs.geo.ves.calc.forward.ForwardSolver
import ru.nucodelabs.geo.ves.calc.graph.MisfitsFunction
import ru.nucodelabs.geo.ves.calc.graph.vesCurvesContext
import ru.nucodelabs.geo.ves.calc.inverse.inverse_functions.SquaresDiff
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
    private val forwardSolver: ForwardSolver
) : AbstractController() {
    @FXML
    private lateinit var  targetFunctionText: Label

    @FXML
    private lateinit var misfitText: Label

    @FXML
    private lateinit var errorText: Label

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
                val targetFun = SquaresDiff()
                val targetFunValue = targetFun.apply(
                    forwardSolver(picket.effectiveExperimentalData, picket.modelData),
                    picket.effectiveExperimentalData.map { it.resistanceApparent }
                )
                targetFunctionText.text =
                    "целевая функция: f = ${dfFour.format(targetFunValue)}" +
                            " | "
                misfitText.text =
                    "отклонение: avg = ${dfFour.format(avgWithoutErr)}, max = ${
                                dfFour.format(
                                    maxWithoutErr
                                )
                            }" + " | "
                errorText.text =
                    "погрешность: avg = ${dfTwo.format(avg)}% , max = ${dfTwo.format(max)}%"
                installTooltipsForTerms()
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

    private fun installTooltipsForTerms() {
        val imageForTargetFunction = Image(
            javaClass.getResourceAsStream("/img/targetFunction.png")
        )
        val  tooltipForTargetFunction = Tooltip()
        tooltipForTargetFunction.text = "Целевая функция - это \n" +
                "функция, значение которой минимизируется \n" +
                "при решении обратной задачи\n"
        tooltipForTargetFunction.graphic = ImageView(imageForTargetFunction)
        tooltipForTargetFunction.contentDisplay = ContentDisplay.BOTTOM
        Tooltip.install(targetFunctionText,  tooltipForTargetFunction.forCharts())
        targetFunctionText.text.forEach { _ -> Tooltip.install(targetFunctionText, tooltipForTargetFunction.forCharts()) }

        val imageForMisfit = Image(
            javaClass.getResourceAsStream("/img/misfit.png")
        )
        val  tooltipForMisfit = Tooltip()
        tooltipForMisfit.text = "Отклонение - это \n" +
                "отклонение теоретических \n" +
                "сигналов от экспериментальных \n"
        tooltipForMisfit.graphic = ImageView(imageForMisfit)
        tooltipForMisfit.contentDisplay = ContentDisplay.BOTTOM
        Tooltip.install(misfitText, tooltipForMisfit.forCharts())
        misfitText.text.forEach { _ -> Tooltip.install(misfitText,tooltipForMisfit.forCharts()) }

        val imageForError = Image(
            javaClass.getResourceAsStream("/img/error.png")
        )
        val  tooltipForError = Tooltip()
        tooltipForError.text = "Погрешность - это \n" +
                "отклонение теоретических сигналов от \n" +
                "экспериментальных в процентах, относительно \n" +
                "погрешности измерения \n"
        tooltipForError.graphic = ImageView(imageForError)
        tooltipForError.contentDisplay = ContentDisplay.BOTTOM
        Tooltip.install(errorText, tooltipForError.forCharts())
        errorText.text.forEach { _ -> Tooltip.install(errorText, tooltipForError.forCharts()) }

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