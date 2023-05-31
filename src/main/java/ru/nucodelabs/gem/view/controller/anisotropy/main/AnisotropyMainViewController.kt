package ru.nucodelabs.gem.view.controller.anisotropy.main

import javafx.beans.binding.Bindings
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.chart.XYChart
import javafx.scene.control.*
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.util.Callback
import javafx.util.StringConverter
import ru.nucodelabs.gem.app.io.saveInitialDirectory
import ru.nucodelabs.gem.app.pref.JSON_FILES_DIR
import ru.nucodelabs.gem.config.ArgNames
import ru.nucodelabs.gem.config.Style
import ru.nucodelabs.gem.fxmodel.anisotropy.ObservableSignal
import ru.nucodelabs.gem.fxmodel.anisotropy.app.AnisotropyFxAppModel
import ru.nucodelabs.gem.fxmodel.anisotropy.app.MapOverlayType
import ru.nucodelabs.gem.fxmodel.map.ObservableWgs
import ru.nucodelabs.gem.util.fx.forCharts
import ru.nucodelabs.gem.util.fx.toObservableList
import ru.nucodelabs.gem.util.std.toDoubleOrNullBy
import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.control.chart.*
import ru.nucodelabs.gem.view.controller.util.indexCellFactory
import ru.nucodelabs.gem.view.mapping.mapAzimuthSignals
import ru.nucodelabs.gem.view.mapping.mapSignals
import ru.nucodelabs.kfx.core.AbstractViewController
import ru.nucodelabs.kfx.ext.bidirectionalNot
import ru.nucodelabs.kfx.ext.observableListOf
import java.io.File
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import java.util.prefs.Preferences
import javax.inject.Inject
import javax.inject.Named

private const val MAP_IMAGE_SIZE = 350
private const val DEFAULT_MAP_IMAGE_SCALE = 1.0
private const val DEFAULT_TRANSPARENCY = 0.5
private const val EXP_SIGNALS = "Экспериментальные сигналы"
private const val ERR_UPPER_SIGNALS = "Верхняя граница погрешности"
private const val ERR_LOWER_SIGNALS = "Нижняя граница погрешности"

class AnisotropyMainViewController @Inject constructor(
    private val appModel: AnisotropyFxAppModel,
    @Named(ArgNames.File.JSON) private val fileChooser: FileChooser,
    private val preferences: Preferences,
    private val colorMapper: ColorMapper,
    @Named(ArgNames.PRECISE) private val preciseDecimalFormat: DecimalFormat,
    private val df: DecimalFormat,
    private val alertsFactory: AlertsFactory,
    private val formatter: StringConverter<Number>,
    private val doubleStringConverter: StringConverter<Double>
) : AbstractViewController<VBox>() {

    @FXML
    lateinit var vesCurves: LineChart<Number, Number>

    @FXML
    lateinit var azimuthDropdown: ComboBox<Double>

    @FXML
    private lateinit var transparencySlider: Slider

    /* SIGNALS TABLE COLUMNS **************************************************************************************************/

    @FXML
    private lateinit var signalsTable: TableView<ObservableSignal>

    @FXML
    private lateinit var voltageCol: TableColumn<ObservableSignal, Double>

    @FXML
    private lateinit var amperageCol: TableColumn<ObservableSignal, Double>

    @FXML
    private lateinit var errorResistanceCol: TableColumn<ObservableSignal, Double>

    @FXML
    private lateinit var resistanceApparentCol: TableColumn<ObservableSignal, Double>

    @FXML
    private lateinit var mn2Col: TableColumn<ObservableSignal, Double>

    @FXML
    private lateinit var ab2Col: TableColumn<ObservableSignal, Double>

    @FXML
    private lateinit var indexCol: TableColumn<Any, Int>

    @FXML
    private lateinit var isHiddenCol: TableColumn<ObservableSignal, Boolean>

    /******************************************************************************************************************/

    @FXML
    private lateinit var mapOverlayType: ComboBox<MapOverlayType>

    @FXML
    private lateinit var centerLongitudeTf: TextField

    @FXML
    private lateinit var centerLatitudeTf: TextField

    @FXML
    private lateinit var signalsMapAxisY: NucodeNumberAxis

    @FXML
    private lateinit var signalsMapAxisX: NucodeNumberAxis

    @FXML
    lateinit var signalsMap: CombinedChart

    @FXML
    lateinit var signalsInterpolation: SmartInterpolationMap

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        initControls()
        initAndSetupListeners()
    }

    private fun initControls() {
        initSignalsMap()


        initAzimuthDropdown()
        initSignalsTable()
        initVesCurves()
    }

    private fun initSignalsMap() {
        mapOverlayType.items += MapOverlayType.values()
        mapOverlayType.selectionModel.select(MapOverlayType.OVERLAY)

        signalsMapAxisY.tickLabelFormatter = formatter
        signalsMapAxisX.tickLabelFormatter = formatter

        signalsMap.data = mapAzimuthSignals(appModel.observablePoint.azimuthSignals)
        signalsMap.dataProperty().bind(
            Bindings.createObjectBinding(
                { mapAzimuthSignals(appModel.observablePoint.azimuthSignals) },
                appModel.observablePoint.azimuthSignals,
            )
        )

        signalsInterpolation.colorMapper = colorMapper
        signalsInterpolation.data = mapAzimuthSignals(appModel.observablePoint.azimuthSignals)

        signalsMap.colorMapper = colorMapper
        signalsInterpolation.colorMapper = colorMapper

        signalsMap.installTooltips(::tooltipFactory)
        signalsInterpolation.installTooltips(::tooltipFactory)
        signalsMap.canvasBlendMode = MapOverlayType.OVERLAY.fxMode
        signalsMap.canvasBlendModeProperty().bind(
            Bindings.createObjectBinding(
                { mapOverlayType.selectionModel.selectedItem.fxMode },
                mapOverlayType.selectionModel.selectedItemProperty()
            )
        )
        transparencySlider.value = DEFAULT_TRANSPARENCY
        signalsMap.canvasOpacityProperty().bind(transparencySlider.valueProperty().asObject())
    }

    private fun initAzimuthDropdown() {
        azimuthDropdown.converter = doubleStringConverter
        azimuthDropdown.items = appModel.observablePoint.azimuthSignals.map { it.azimuth }.toObservableList()
        azimuthDropdown.itemsProperty().bind(
            Bindings.createObjectBinding(
                { appModel.observablePoint.azimuthSignals.map { it.azimuth }.toObservableList() },
                appModel.observablePoint.azimuthSignals
            )
        )
        azimuthDropdown.selectionModel.selectedItemProperty().addListener { _, _, new ->
            appModel.selectAzimuth(new ?: 0.0)
        }
        azimuthDropdown.itemsProperty().addListener { _, _, _ ->
            azimuthDropdown.selectionModel.selectFirst()
        }
    }

    private fun initSignalsTable() {
        setupSignalsTableCellValueFactories()
        setupSignalsTableCellFactories()

        signalsTable.itemsProperty().bind(
            Bindings.createObjectBinding(
                { appModel.selectedObservableSignals?.signals?.sortedSignals ?: observableListOf() },
                appModel.observablePoint.azimuthSignals,
                appModel.selectedAzimuthProperty()
            )
        )
    }

    private fun initVesCurves() {
        vesCurves.installLegendStyleAccordingToSeries()
        vesCurves.data = observableListOf(
            mapSignals(appModel.selectedObservableSignals?.signals?.effectiveSignals ?: emptyList())
        )
        vesCurves.dataProperty().bind(
            Bindings.createObjectBinding(
                {
                    observableListOf(
                        mapSignals(appModel.selectedObservableSignals?.signals?.effectiveSignals ?: emptyList()).apply {
                            name = EXP_SIGNALS
                            lineStyle(Style.Class.TRANSPARENT_LINE)
                            symbolStyle(Style.Class.EXP_SYMBOL)
                        },
                        mapSignals(appModel.upperErrorBoundSignals()).apply {
                            name = ERR_UPPER_SIGNALS
                            lineStyle(Style.Class.TRANSPARENT_LINE)
                            symbolStyle(Style.Class.EXP_ERROR_UPPER_SYMBOL)
                        },
                        mapSignals(appModel.lowerErrorBoundSignals()).apply {
                            name = ERR_LOWER_SIGNALS
                            lineStyle(Style.Class.TRANSPARENT_LINE)
                            symbolStyle(Style.Class.EXP_ERROR_LOWER_SYMBOL)
                        },
                    )
                },
                appModel.observablePoint.azimuthSignals,
                appModel.selectedAzimuthProperty()
            )
        )

    }

    private fun setupSignalsTableCellValueFactories() {
        isHiddenCol.cellValueFactory = Callback { features -> features.value.hiddenProperty().bidirectionalNot() }
        ab2Col.cellValueFactory = Callback { features -> features.value.ab2Property().asObject() }
        mn2Col.cellValueFactory = Callback { features -> features.value.mn2Property().asObject() }
        resistanceApparentCol.cellValueFactory =
            Callback { features -> features.value.resistanceApparentProperty().asObject() }
        errorResistanceCol.cellValueFactory =
            Callback { features -> features.value.errorResistanceApparentProperty().asObject() }
        amperageCol.cellValueFactory = Callback { features -> features.value.amperageProperty().asObject() }
        voltageCol.cellValueFactory = Callback { features -> features.value.voltageProperty().asObject() }
    }

    private fun setupSignalsTableCellFactories() {
        indexCol.cellFactory = indexCellFactory()

        isHiddenCol.cellFactory = CheckBoxTableCell.forTableColumn(isHiddenCol)

        val editableColumns = listOf(
            ab2Col,
            mn2Col,
            resistanceApparentCol,
            errorResistanceCol,
            amperageCol,
            voltageCol
        )
        editableColumns.forEach { it.cellFactory = TextFieldTableCell.forTableColumn(doubleStringConverter) }
    }

    private fun initAndSetupListeners() {
        signalsInterpolation.data = mapAzimuthSignals(appModel.observablePoint.azimuthSignals)

        signalsInterpolation.dataProperty().bind(
            Bindings.createObjectBinding(
                { mapAzimuthSignals(appModel.observablePoint.azimuthSignals) },
                appModel.observablePoint.azimuthSignals,
            )
        )

        updatePointCenterTextFields()
        updateSignalsMapImage()
        appModel.observablePoint.centerProperty().addListener { _, _, _ ->
            updateSignalsMapImage()
            updatePointCenterTextFields()
        }
        appModel.observablePoint.azimuthSignals.addListener(ListChangeListener { updateSignalsMapImage() })
    }

    private fun updateSignalsMapImage() {
        val mapImage = appModel.mapImage(MAP_IMAGE_SIZE, DEFAULT_MAP_IMAGE_SCALE)

        if (mapImage != null) {
            signalsMapAxisX.lowerBound = mapImage.xLowerBound
            signalsMapAxisX.upperBound = mapImage.xUpperBound

            signalsMapAxisX.forceMarks.add(0.0)
            signalsMapAxisX.forceMarks.add(0.0)

            signalsMapAxisY.lowerBound = mapImage.yLowerBound
            signalsMapAxisY.upperBound = mapImage.yUpperBound

            signalsMapAxisY.forceMarks.add(0.0)
            signalsMapAxisY.forceMarks.add(0.0)
        }

        signalsMap.image = mapImage?.image
    }

    private fun updatePointCenterTextFields() {
        centerLatitudeTf.text = preciseDecimalFormat.format(appModel.observablePoint.center?.latitudeInDegrees ?: 0)
        centerLongitudeTf.text = preciseDecimalFormat.format(appModel.observablePoint.center?.longitudeInDegrees ?: 0)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun tooltipFactory(
        seriesIndex: Int,
        series: XYChart.Series<Number, Number>,
        pointIndex: Int,
        point: XYChart.Data<Number, Number>
    ): Tooltip {
        val index = pointIndex / 2
        val azimuthSignal = appModel.observablePoint.azimuthSignals[0].signals.sortedSignals[index].ab2
        val resistance =
            appModel.observablePoint.azimuthSignals[seriesIndex].signals.effectiveSignals[index].resistanceApparent
        val azimuth: Double = if (pointIndex % 2 == 0) {
            appModel.observablePoint.azimuthSignals[seriesIndex].azimuth
        } else {
            appModel.observablePoint.azimuthSignals[seriesIndex].azimuth + 180
        }
        val tooltipText = """
            № ${index + 1}
            AB/2 = ${df.format(azimuthSignal)} m
            ρₐ = ${df.format(resistance)} Ω‧m
            Азимут = ${df.format(azimuth)} °
        """.trimIndent()
        return Tooltip(tooltipText).forCharts()
    }

    @FXML
    private fun loadProject() {
        val file: File? = fileChooser.showOpenDialog(stage)
        if (file != null) {
            saveInitialDirectory(preferences, JSON_FILES_DIR, fileChooser, file)
            appModel.loadProject(file)
        }
    }

    @FXML
    private fun saveProject() {
        val file: File? = fileChooser.showSaveDialog(stage)
        if (file != null) {
            saveInitialDirectory(preferences, JSON_FILES_DIR, fileChooser, file)
            appModel.saveProject(file)
        }
    }

    @FXML
    private fun modifyPointCenter() {
        val newLatitude = centerLatitudeTf.text.toDoubleOrNullBy(preciseDecimalFormat)
        val newLongitude = centerLongitudeTf.text.toDoubleOrNullBy(preciseDecimalFormat)

        if (newLatitude == null || newLongitude == null) {
            alertsFactory.simpleAlert(text = "Введите вещественные числа").show()
        }

        if (newLatitude != null && newLongitude != null) {
            try {
                appModel.editCenter(ObservableWgs(newLongitude, newLatitude))
            } catch (e: Exception) {
                alertsFactory.simpleExceptionAlert(e).show()
            }
        }
    }

    @FXML
    private fun undo() {
        appModel.undo()
    }

    @FXML
    private fun redo() {
        appModel.redo()
    }

    @FXML
    private fun newProject() {
        appModel.newProject()
    }
}