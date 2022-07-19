package ru.nucodelabs.gem.view.charts

import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.chart.XYChart.Data
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.CheckBox
import javafx.scene.control.ContextMenu
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.input.ContextMenuEvent
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.StringConverter
import ru.nucodelabs.gem.app.pref.*
import ru.nucodelabs.gem.extensions.fx.bindTo
import ru.nucodelabs.gem.extensions.fx.isValidBy
import ru.nucodelabs.gem.extensions.fx.observableListOf
import ru.nucodelabs.gem.extensions.fx.toObservableList
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.control.chart.NucodeNumberAxis
import ru.nucodelabs.gem.view.control.chart.PolygonChart
import ru.nucodelabs.gem.view.control.chart.log.LogarithmicAxis
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
    private lateinit var root: VBox

    @FXML
    private lateinit var configWindow: Stage

    @FXML
    private lateinit var minValueSpinner: Spinner<Double>

    @FXML
    private lateinit var maxValueSpinner: Spinner<Double>

    @FXML
    private lateinit var numberOfSegmentsSpinner: Spinner<Int>

    @FXML
    private lateinit var isLogChkBox: CheckBox

    @FXML
    private lateinit var ctxMenu: ContextMenu

    @FXML
    private lateinit var linearYAxis: NucodeNumberAxis

    @FXML
    private lateinit var logYAxis: LogarithmicAxis

    @FXML
    private lateinit var linearChart: PolygonChart

    @FXML
    private lateinit var logChart: PolygonChart

    override val stage: Stage?
        get() = root.scene.window as Stage?

    private val formatterProperty: ReadOnlyObjectProperty<StringConverter<Number>> =
        SimpleObjectProperty(stringConverter)

    fun formatterProperty() = formatterProperty
    val formatter = formatterProperty

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        colorMapper.minValueProperty().addListener { _, _, _ -> update() }
        colorMapper.maxValueProperty().addListener { _, _, _ -> update() }
        colorMapper.numberOfSegmentsProperty().addListener { _, _, _ -> update() }

        linearChart.data = observableListOf()

        setupControls()
        setupCharts()
        setupAxisBounds()
    }

    private fun setupAxisBounds() {
        linearYAxis.lowerBoundProperty() bindTo colorMapper.minValueProperty()
        linearYAxis.upperBoundProperty() bindTo colorMapper.maxValueProperty()

        logYAxis.lowerBoundProperty() bindTo linearYAxis.lowerBoundProperty()
        logYAxis.upperBoundProperty() bindTo linearYAxis.upperBoundProperty()

//        linearYAxis.tickUnitProperty().bind(linearYAxis.rangeBinding().divide(colorMapper.numberOfSegmentsProperty()))
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

        initConfig()
    }

    private fun setupCharts() {
        colorMapper.minValueProperty().bind(minValueSpinner.valueProperty())
        colorMapper.maxValueProperty().bind(maxValueSpinner.valueProperty())
        colorMapper.numberOfSegmentsProperty().bind(numberOfSegmentsSpinner.valueProperty())
        isLogChkBox.isSelected = colorMapper.isLogScale
        colorMapper.logScaleProperty().bind(isLogChkBox.selectedProperty())

        linearChart.visibleProperty() bindTo !isLogChkBox.selectedProperty()
        linearChart.managedProperty() bindTo linearChart.visibleProperty()

        logChart.visibleProperty() bindTo !linearChart.visibleProperty()
        logChart.managedProperty() bindTo !linearChart.managedProperty()
        logChart.dataProperty() bindTo linearChart.dataProperty()
    }

    @Suppress("UNCHECKED_CAST")
    private fun update() {
        val range = colorMapper.maxValue - colorMapper.minValue
        linearChart.data = colorMapper.segments.map {
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

        linearChart.data.forEachIndexed { index, series ->
            linearChart.seriesPolygons[series]?.apply { fill = colorMapper.segments[index].color }
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