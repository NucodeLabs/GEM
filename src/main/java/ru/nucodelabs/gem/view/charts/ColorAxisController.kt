package ru.nucodelabs.gem.view.charts

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.chart.XYChart.Data
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.ContextMenu
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.input.ContextMenuEvent
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.StringConverter
import ru.nucodelabs.gem.app.pref.*
import ru.nucodelabs.gem.extensions.fx.isValidBy
import ru.nucodelabs.gem.extensions.fx.observableListOf
import ru.nucodelabs.gem.extensions.fx.toObservableList
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.control.chart.NucodeNumberAxis
import ru.nucodelabs.gem.view.control.chart.PolygonChart
import ru.nucodelabs.gem.view.control.chart.limitTickLabelsWidth
import ru.nucodelabs.gem.view.control.chart.rangeBinding
import java.net.URL
import java.util.*
import java.util.prefs.Preferences
import javax.inject.Inject

class ColorAxisController @Inject constructor(
    private val colorMapper: ColorMapper,
    private val fxPreferences: FXPreferences,
    private val preferences: Preferences,
    private val stringConverter: StringConverter<Number>
) : AbstractController() {

    private val minAndMaxRange = 0.0..100_000.0
    private val segmentsRange = 2..100

    @FXML
    private lateinit var configWindow: Stage

    @FXML
    private lateinit var minValueSpinner: Spinner<Double>

    @FXML
    private lateinit var maxValueSpinner: Spinner<Double>

    @FXML
    private lateinit var numberOfSegmentsSpinner: Spinner<Int>

    @FXML
    private lateinit var ctxMenu: ContextMenu

    @FXML
    private lateinit var yAxis: NucodeNumberAxis

    @FXML
    private lateinit var chart: PolygonChart
    override val stage: Stage?
        get() = chart.scene.window as Stage?

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        colorMapper.minValueProperty().addListener { _, _, _ -> update() }
        colorMapper.maxValueProperty().addListener { _, _, _ -> update() }
        colorMapper.numberOfSegmentsProperty().addListener { _, _, _ -> update() }

        setupControls()
        setupAxis()
        update()
    }

    private fun setupAxis() {
        yAxis.tickLabelFormatter = stringConverter
        yAxis.tickUnitProperty().bind(yAxis.rangeBinding().divide(colorMapper.numberOfSegmentsProperty()))
        yAxis.limitTickLabelsWidth(35.0)
    }

    private fun lazyConfigWindowInitOwner() {
        if (configWindow.owner == null) {
            configWindow.initOwner(stage)
        }
    }

    private fun initConfig() {
        fxPreferences.bind(maxValueSpinner.valueProperty(), COLOR_MAX_VALUE.key, COLOR_MAX_VALUE.def)
        fxPreferences.bind(minValueSpinner.valueProperty(), COLOR_MIN_VALUE.key, COLOR_MIN_VALUE.def)
        fxPreferences.bind(numberOfSegmentsSpinner.valueProperty(), COLOR_SEGMENTS.key, COLOR_SEGMENTS.def)
    }

    private fun setupControls() {
        configWindow.initStyle(StageStyle.UTILITY)

        val step = 10.0
        val doubleValueFactory = { pref: Preference<Double> ->
            SpinnerValueFactory.DoubleSpinnerValueFactory(
                0.0,
                100_000.0,
                preferences.getDouble(pref.key, pref.def),
                step
            )
        }

        // TODO: Исправить NPE при вводе не цифровых символов в спиннерах. Он вызывает commitValue() при потере фокуса
        with(minValueSpinner) {
            valueFactory = doubleValueFactory(COLOR_MIN_VALUE)
            val valid = editor.isValidBy {
                it.toDoubleOrNull()?.let { parsed -> parsed in minAndMaxRange } ?: false
            }
            editor.onAction = EventHandler { if (valid.get()) commitValue() }
        }
        with(maxValueSpinner) {
            valueFactory = doubleValueFactory(COLOR_MAX_VALUE)
            val valid = editor.isValidBy {
                it.toDoubleOrNull()?.let { parsed -> parsed in minAndMaxRange } ?: false
            }
            editor.onAction = EventHandler { if (valid.get()) commitValue() }
        }
        with(numberOfSegmentsSpinner) {
            valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(
                2,
                100,
                preferences.getInt(COLOR_SEGMENTS.key, COLOR_SEGMENTS.def),
                1,
            )
            val valid = numberOfSegmentsSpinner.editor.isValidBy {
                it.toIntOrNull()?.let { parsed -> parsed in segmentsRange } ?: false
            }
            editor.onAction = EventHandler { if (valid.get()) numberOfSegmentsSpinner.commitValue() }
        }

        colorMapper.minValueProperty().bind(minValueSpinner.valueProperty())
        colorMapper.maxValueProperty().bind(maxValueSpinner.valueProperty())
        colorMapper.numberOfSegmentsProperty().bind(numberOfSegmentsSpinner.valueProperty())

        yAxis.lowerBoundProperty().bind(colorMapper.minValueProperty())
        yAxis.upperBoundProperty().bind(colorMapper.maxValueProperty())

        initConfig()
    }

    @Suppress("UNCHECKED_CAST")
    private fun update() {
        val range = colorMapper.maxValue - colorMapper.minValue
        chart.data = colorMapper.segments.map {
            Series(
                observableListOf(
                    Data(0.0, colorMapper.minValue + it.from * range),
                    Data(100.0, colorMapper.minValue + it.from * range),
                    Data(100.0, colorMapper.minValue + it.to * range),
                    Data(0.0, colorMapper.minValue + it.to * range)
                )
            ) as Series<Number, Number>
            // safe upcast Double : Number
        }.toObservableList()

        chart.data.forEachIndexed { index, series ->
            chart.seriesPolygons[series]?.apply { fill = colorMapper.segments[index].color }
        }
    }

    @FXML
    private fun openConfiguration() {
        lazyConfigWindowInitOwner()
        configWindow.show()
    }

    @FXML
    private fun openContextMenu(event: ContextMenuEvent) {
        ctxMenu.show(stage, event.screenX, event.screenY)
    }
}