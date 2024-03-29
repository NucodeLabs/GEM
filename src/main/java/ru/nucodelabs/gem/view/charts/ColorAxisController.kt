package ru.nucodelabs.gem.view.charts

import javafx.beans.property.ObjectProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.chart.XYChart.Data
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.*
import javafx.scene.input.ContextMenuEvent
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.util.StringConverter
import ru.nucodelabs.gem.app.pref.*
import ru.nucodelabs.gem.util.fx.*
import ru.nucodelabs.gem.util.std.toDoubleOrNullBy
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.control.chart.NucodeNumberAxis
import ru.nucodelabs.gem.view.control.chart.PolygonChart
import ru.nucodelabs.gem.view.control.chart.limitTickLabelsWidth
import ru.nucodelabs.gem.view.control.chart.log.LogarithmicAxis
import java.io.File
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import java.util.prefs.Preferences
import javax.inject.Inject
import javax.inject.Named


class ColorAxisController @Inject constructor(
    @Named("CLR") private val clrFile: File,
    private val colorMapper: ColorMapper,
    private val fxPreferences: FXPreferences,
    private val stringConverter: StringConverter<Number>,
    private val decimalFormat: DecimalFormat,
    @Named("PNG") private val fc: FileChooser,
    private val prefs: Preferences
) : AbstractController() {

    private val minAndMaxRange = 0.1..100_000.0
    private val segmentsRange = 2..100

    @FXML
    private lateinit var root: VBox

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
    private lateinit var linearYAxis: NucodeNumberAxis

    @FXML
    private lateinit var logYAxis: LogarithmicAxis

    @FXML
    private lateinit var linearChart: PolygonChart

    @FXML
    private lateinit var logChart: PolygonChart

    override val stage: Stage?
        get() = root.scene.window as Stage?

    override fun initialize(location: URL, resources: ResourceBundle) {
        fileLbl.text = "Цветовая схема: ${clrFile.path}"
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

    private fun setupAxes() {
        linearYAxis.lowerBoundProperty() bindTo colorMapper.minValueProperty()
        linearYAxis.upperBoundProperty() bindTo colorMapper.maxValueProperty()

        logYAxis.lowerBoundProperty() bindTo linearYAxis.lowerBoundProperty()
        logYAxis.upperBoundProperty() bindTo linearYAxis.upperBoundProperty()

        linearYAxis.tickLabelFormatter = stringConverter
        logYAxis.tickLabelFormatter = stringConverter
        linearYAxis.limitTickLabelsWidth(35.0)
        logYAxis.limitTickLabelsWidth(35.0)

        linearYAxis.tickUnitProperty() bindTo (colorMapper.maxValueProperty() - colorMapper.minValueProperty()) / colorMapper.numberOfSegmentsProperty()
    }

    private fun lazyConfigWindowInitOwner() {
        if (configWindow.owner == null) {
            configWindow.initOwner(stage)
        }
        configWindow.icons.setAll(stage?.icons)
    }

    @Suppress("UNCHECKED_CAST")
    private fun initConfig() {
        maxValueTf.textFormatter.value = fxPreferences.bind(
            maxValueTf.textFormatter.valueProperty() as ObjectProperty<Double>,
            COLOR_MAX_VALUE.key,
            COLOR_MAX_VALUE.def
        )
        minValueTf.textFormatter.value = fxPreferences.bind(
            minValueTf.textFormatter.valueProperty() as ObjectProperty<Double>,
            COLOR_MIN_VALUE.key,
            COLOR_MIN_VALUE.def
        )
        numberOfSegmentsTf.textFormatter.value = fxPreferences.bind(
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
                    observableListOf(
                        Data(0.0, it.from),
                        Data(100.0, it.from),
                        Data(100.0, it.to),
                        Data(0.0, it.to)
                    )
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