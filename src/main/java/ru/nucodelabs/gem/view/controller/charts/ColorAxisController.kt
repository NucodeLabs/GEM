package ru.nucodelabs.gem.view.controller.charts

import jakarta.inject.Inject
import jakarta.inject.Named
import javafx.beans.property.ObjectProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.geometry.Side
import javafx.scene.chart.XYChart.Data
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.*
import javafx.scene.input.ContextMenuEvent
import javafx.scene.layout.Pane
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.util.StringConverter
import ru.nucodelabs.gem.app.pref.COLOR_MAX_VALUE
import ru.nucodelabs.gem.app.pref.COLOR_MIN_VALUE
import ru.nucodelabs.gem.app.pref.COLOR_SEGMENTS
import ru.nucodelabs.gem.app.pref.PNG_FILES_DIR
import ru.nucodelabs.gem.config.Name
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.control.chart.NucodeNumberAxis
import ru.nucodelabs.gem.view.control.chart.PolygonChart
import ru.nucodelabs.gem.view.control.chart.limitTickLabelsWidth
import ru.nucodelabs.gem.view.control.chart.log.LogarithmicAxis
import ru.nucodelabs.kfx.core.AbstractViewController
import ru.nucodelabs.kfx.ext.*
import ru.nucodelabs.kfx.pref.FXPreferences
import ru.nucodelabs.util.std.toDoubleOrNullBy
import tornadofx.div
import tornadofx.minus
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import java.util.prefs.Preferences


class ColorAxisController @Inject constructor(
    @Named(Name.CLR_SOURCE) private val clrFilePath: String,
    private val colorMapper: ColorMapper,
    private val fxPreferences: FXPreferences,
    private val stringConverter: StringConverter<Number>,
    private val decimalFormat: DecimalFormat,
    @Named(Name.File.PNG) private val fc: FileChooser,
    private val prefs: Preferences
) : AbstractViewController<Pane>() {

    private val minAndMaxRange = 0.1..100_000.0
    private val segmentsRange = 2..100

    @FXML
    private lateinit var configWindow: Stage

    @FXML
    private lateinit var fileLbl: Label

    @FXML
    private lateinit var minValueTf: TextField

    @FXML
    private lateinit var maxValueTf: TextField

    @FXML
    private lateinit var numberOfSegmentsTf: TextField

    @FXML
    private lateinit var isLogChkBox: CheckBox

    @FXML
    private lateinit var ctxMenu: ContextMenu

    @FXML
    private lateinit var axis: NucodeNumberAxis

    @FXML
    private lateinit var logAxis: LogarithmicAxis

    @FXML
    private lateinit var linearChart: PolygonChart

    @FXML
    private lateinit var logChart: PolygonChart

    override fun initialize(location: URL, resources: ResourceBundle) {
        fileLbl.text = "Цветовая схема: $clrFilePath"
        colorMapper.minValueProperty().addListener { _, _, _ -> update() }
        colorMapper.maxValueProperty().addListener { _, _, _ -> update() }
        colorMapper.numberOfSegmentsProperty().addListener { _, _, _ -> update() }
        colorMapper.logScaleProperty().addListener { _, _, _ -> update() }

        linearChart.data = observableListOf()

        ctxMenu.items += MenuItem("Сохранить как изображение").apply {
            onAction = EventHandler {
                (if (isLogChkBox.isSelected) logChart else linearChart).saveSnapshotAsPng(fc)?.also {
                    if (it.parentFile.isDirectory) {
                        prefs.put(PNG_FILES_DIR.key, it.parentFile.absolutePath)
                    }
                }
            }
        }

        setupControls()
        initConfig()
        setupCharts()
        setupAxes()
        update()
    }

    private fun isVerticalAxis(): Boolean {
        return axis.side == Side.LEFT || axis.side == Side.RIGHT
    }

    private fun setupAxes() {
        axis.lowerBoundProperty() bindTo colorMapper.minValueProperty()
        axis.upperBoundProperty() bindTo colorMapper.maxValueProperty()

        logAxis.lowerBoundProperty() bindTo axis.lowerBoundProperty()
        logAxis.upperBoundProperty() bindTo axis.upperBoundProperty()

        axis.tickLabelFormatter = stringConverter
        logAxis.tickLabelFormatter = stringConverter
        axis.limitTickLabelsWidth(35.0)
        logAxis.limitTickLabelsWidth(35.0)

        axis.tickUnitProperty() bindTo (colorMapper.maxValueProperty() - colorMapper.minValueProperty()) / colorMapper.numberOfSegmentsProperty()
    }

    private fun lazyConfigWindowInitOwner() {
        if (configWindow.owner == null) {
            configWindow.initOwner(stage)
        }
        configWindow.icons.setAll(stage?.icons)
    }

    @Suppress("UNCHECKED_CAST")
    private fun initConfig() {
        (maxValueTf.textFormatter as TextFormatter<Double>).value = fxPreferences.bind(
            maxValueTf.textFormatter.valueProperty() as ObjectProperty<Double>,
            COLOR_MAX_VALUE.key,
            COLOR_MAX_VALUE.def
        )

        (minValueTf.textFormatter as TextFormatter<Double>).value = fxPreferences.bind(
            minValueTf.textFormatter.valueProperty() as ObjectProperty<Double>,
            COLOR_MIN_VALUE.key,
            COLOR_MIN_VALUE.def
        )
        (numberOfSegmentsTf.textFormatter as TextFormatter<Int>).value = fxPreferences.bind(
            numberOfSegmentsTf.textFormatter.valueProperty() as ObjectProperty<Int>,
            COLOR_SEGMENTS.key,
            COLOR_SEGMENTS.def
        )
    }

    private fun setupControls() {

        val doubleConverter = DoubleValidationConverter(decimalFormat) { it in minAndMaxRange }
        with(minValueTf) {
            textFormatter = TextFormatter(doubleConverter, COLOR_MIN_VALUE.def, decimalFilter(decimalFormat))
            isValidBy(blankIsValid = false) {
                it.toDoubleOrNullBy(decimalFormat)?.let { parsed -> parsed in minAndMaxRange } ?: false
            }
        }
        with(maxValueTf) {
            textFormatter = TextFormatter(doubleConverter, COLOR_MAX_VALUE.def, decimalFilter(decimalFormat))
            isValidBy(blankIsValid = false) {
                it.toDoubleOrNullBy(decimalFormat)?.let { parsed -> parsed in minAndMaxRange } ?: false
            }
        }

        val intConverter = IntValidationConverter { it in segmentsRange }
        with(numberOfSegmentsTf) {
            textFormatter = TextFormatter(intConverter, COLOR_SEGMENTS.def, intFilter())
            isValidBy(blankIsValid = false) { it.toIntOrNull()?.let { parsed -> parsed in segmentsRange } ?: false }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun setupCharts() {
        colorMapper.minValueProperty().bind(minValueTf.textFormatter.valueProperty() as ObjectProperty<Double>)
        colorMapper.maxValueProperty().bind(maxValueTf.textFormatter.valueProperty() as ObjectProperty<Double>)
        colorMapper.numberOfSegmentsProperty()
            .bind(numberOfSegmentsTf.textFormatter.valueProperty() as ObjectProperty<Int>)
        isLogChkBox.isSelected = colorMapper.isLogScale
        colorMapper.logScaleProperty().bind(isLogChkBox.selectedProperty())

        linearChart.visibleProperty() bindTo !isLogChkBox.selectedProperty()
        linearChart.managedProperty() bindTo linearChart.visibleProperty()

        logChart.visibleProperty() bindTo !linearChart.visibleProperty()
        logChart.managedProperty() bindTo !linearChart.managedProperty()
    }

    @Suppress("UNCHECKED_CAST")
    private fun update() {
        with(if (isLogChkBox.isSelected) logChart else linearChart) {
            data.setAll(colorMapper.segments.map {
                Series(
                    if (isVerticalAxis()) {
                        observableListOf(
                            Data(0.0, it.from),
                            Data(100.0, it.from),
                            Data(100.0, it.to),
                            Data(0.0, it.to)
                        )
                    } else {
                        observableListOf(
                            Data(it.from, 0.0),
                            Data(it.from, 100.0),
                            Data(it.to, 100.0),
                            Data(it.to, 0.0)
                        )
                    }
                ) as Series<Number, Number>
                // safe upcast Double : Number
            }.toObservableList())

            data.forEachIndexed { index, series ->
                seriesPolygons[series]?.apply { fill = colorMapper.segments[index].color }
            }
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