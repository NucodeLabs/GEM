package ru.nucodelabs.gem.view.controller.charts

import jakarta.inject.Inject
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ObservableObjectValue
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import ru.nucodelabs.gem.fxmodel.ves.app.VesFxAppModel
import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.gem.view.control.chart.log.LogarithmicAxis
import ru.nucodelabs.geo.ves.Picket
import ru.nucodelabs.geo.ves.calc.graph.vesCurvesContext
import ru.nucodelabs.kfx.core.AbstractViewController
import ru.nucodelabs.kfx.ext.Point
import ru.nucodelabs.kfx.ext.forCharts
import ru.nucodelabs.kfx.ext.line
import ru.nucodelabs.kfx.ext.observableListOf
import java.math.RoundingMode
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import kotlin.math.abs

const val TARGET_FUNCTION_DESCRIPTION = "Целевая функция - это \n" +
        "функция, значение которой минимизируется \n" +
        "при решении обратной задачи\n"
const val MISFIT_DESCRIPTION = "Отклонение - это \n" +
        "отклонение теоретических \n" +
        "сигналов от экспериментальных \n"
const val ERROR_DESCRIPTION = "Погрешность - это \n" +
        "отклонение теоретических сигналов от \n" +
        "экспериментальных в процентах, относительно \n" +
        "погрешности измерения \n"
const val TARGET_FUNCTION_FORMULA_IMAGE_PATH = "/img/targetFunction.png"
const val MISFIT_FORMULA_IMAGE_PATH ="/img/misfit.png"
const val ERROR_FORMULA_IMAGE_PATH = "/img/error.png"

class MisfitStacksController @Inject constructor(
    private val picketObservable: ObservableObjectValue<Picket>,
    private val alertsFactory: AlertsFactory,
    private val dataProperty: ObjectProperty<ObservableList<Series<Number, Number>>>,
    private val decimalFormat: DecimalFormat,
    private val appModel: VesFxAppModel,
) : AbstractViewController<VBox>() {
    @FXML
    private lateinit var targetFunctionText: Label

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

    private val picket: Picket
        get() = picketObservable.get()!!

    override fun initialize(location: URL, resources: ResourceBundle) {
        picketObservable.addListener { _, _, newValue: Picket? ->
            if (newValue != null) {
                update()
            }
        }
        lineChart.dataProperty().bind(dataProperty)
        installTooltipForTerms()
    }

    private fun update() {
        var misfitStacksSeriesList: MutableList<Series<Number, Number>> = ArrayList()
        try {
            val misfits = appModel.misfits()
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

                val targetFunction = appModel.targetFunction()
                val (misfitsAvg, misfitsMax) = appModel.misfitsAvgMax()
                val (errorAvg, errorMax) = appModel.errorAvgMax()

                val dfTwo = DecimalFormat("#.##").apply { roundingMode = RoundingMode.HALF_UP }
                val dfFour = DecimalFormat("#.####").apply { roundingMode = RoundingMode.HALF_UP }

                targetFunctionText.text =
                    "целевая функция: f = ${dfFour.format(targetFunction)}"
                misfitText.text =
                    "отклонение: avg = ${dfTwo.format(misfitsAvg)}%, max = ${dfTwo.format(misfitsMax)}%"
                errorText.text =
                    "погрешность: avg = ${dfTwo.format(errorAvg)}% , max = ${dfTwo.format(errorMax)}%"
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

    private fun installTooltipForTerms() {
        val imageForTargetFunction = Image(
            javaClass.getResourceAsStream(TARGET_FUNCTION_FORMULA_IMAGE_PATH)
        )
        val  tooltipForTargetFunction = Tooltip()
        tooltipForTargetFunction.text = TARGET_FUNCTION_DESCRIPTION
        tooltipForTargetFunction.graphic = ImageView(imageForTargetFunction)
        tooltipForTargetFunction.contentDisplay = ContentDisplay.BOTTOM
        Tooltip.install(targetFunctionText,  tooltipForTargetFunction.forCharts())

        val imageForMisfit = Image(
            javaClass.getResourceAsStream(MISFIT_FORMULA_IMAGE_PATH)
        )
        val tooltipForMisfit = Tooltip()
        tooltipForMisfit.text = MISFIT_DESCRIPTION
        tooltipForMisfit.graphic = ImageView(imageForMisfit)
        tooltipForMisfit.contentDisplay = ContentDisplay.BOTTOM
        Tooltip.install(misfitText, tooltipForMisfit.forCharts())

        val imageForError = Image(
            javaClass.getResourceAsStream(ERROR_FORMULA_IMAGE_PATH)
        )
        val tooltipForError = Tooltip()
        tooltipForError.text = ERROR_DESCRIPTION
        tooltipForError.graphic = ImageView(imageForError)
        tooltipForError.contentDisplay = ContentDisplay.BOTTOM
        Tooltip.install(errorText, tooltipForError.forCharts())
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