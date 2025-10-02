package ru.nucodelabs.gem.view.controller.main

import jakarta.inject.Inject
import jakarta.inject.Named
import javafx.beans.binding.Bindings.createStringBinding
import javafx.beans.property.IntegerProperty
import javafx.beans.value.ObservableObjectValue
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.*
import javafx.stage.Stage
import javafx.util.Callback
import javafx.util.StringConverter
import ru.nucodelabs.gem.config.Name
import ru.nucodelabs.gem.fxmodel.ves.ObservableExperimentalData
import ru.nucodelabs.gem.fxmodel.ves.ObservableSection
import ru.nucodelabs.gem.fxmodel.ves.mapper.VesFxModelMapper
import ru.nucodelabs.gem.view.controller.AbstractController
import ru.nucodelabs.gem.view.controller.util.indexCellFactory
import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.Picket
import ru.nucodelabs.geo.ves.Section
import ru.nucodelabs.geo.ves.calc.error.*
import ru.nucodelabs.kfx.ext.DoubleValidationConverter
import ru.nucodelabs.kfx.ext.get
import ru.nucodelabs.kfx.ext.observableListOf
import ru.nucodelabs.kfx.ext.toObservableList
import ru.nucodelabs.kfx.snapshot.HistoryManager
import java.net.URL
import java.text.DecimalFormat
import java.util.*

const val DEFAULT_DIST_A_ERROR = 0.5
const val DEFAULT_DIST_B_ERROR = 1e-3 // m

const val DEFAULT_U_A_ERROR = 0.5
const val DEFAULT_U_B_ERROR = 0.5 // mV

const val DEFAULT_I_A_ERROR = 0.5
const val DEFAULT_I_B_ERROR = 0.5 // mA

const val LEQ_SIGN = '≤'

class CalculateErrorScreenController @Inject constructor(
    @Named(Name.PRECISE_FORMAT) private val preciseFormat: DecimalFormat,
    private val df: DecimalFormat,
    private val observableSection: ObservableSection,
    private val historyManager: HistoryManager<Section>,
    private val observablePicket: ObservableObjectValue<Picket>,
    private val picketIndexProp: IntegerProperty,
    private val vesFxModelMapper: VesFxModelMapper,
    private val uiProps: ResourceBundle
) : AbstractController() {
    @FXML
    private lateinit var errorFormula: ComboBox<ErrorFunctionType>

    @FXML
    private lateinit var resAvgCol: TableColumn<ObservableExperimentalData, String>

    @FXML
    private lateinit var chart: LineChart<Number, Number>

    @FXML
    private lateinit var errorResCol: TableColumn<ObservableExperimentalData, String>

    @FXML
    private lateinit var resCol: TableColumn<ObservableExperimentalData, String>

    @FXML
    private lateinit var voltageCol: TableColumn<ObservableExperimentalData, String>

    @FXML
    private lateinit var amperageCol: TableColumn<ObservableExperimentalData, String>

    @FXML
    private lateinit var kCol: TableColumn<ObservableExperimentalData, String>

    @FXML
    private lateinit var mn2Col: TableColumn<ObservableExperimentalData, String>

    @FXML
    private lateinit var ab2Col: TableColumn<ObservableExperimentalData, String>

    @FXML
    private lateinit var indexCol: TableColumn<Any, Int>

    @FXML
    private lateinit var table: TableView<ObservableExperimentalData>

    @FXML
    private lateinit var iBErrorTf: TextField

    @FXML
    private lateinit var iAErrorTf: TextField

    @FXML
    private lateinit var uBErrorTf: TextField

    @FXML
    private lateinit var uAErrorTf: TextField

    @FXML
    private lateinit var distBErrorTf: TextField

    @FXML
    private lateinit var distAErrorTf: TextField

    @FXML
    private lateinit var root: Stage

    override val stage: Stage
        get() = root

    val data = observableListOf<ExperimentalData>()

    private enum class ErrorFunctionType {
        USING_VARIANCE,
        USING_TRUST_INTERVALS
    }

    private lateinit var errorFunctionVersion: ErrorFunctionType

    override fun initialize(location: URL, resources: ResourceBundle) {
        table.selectionModel.isCellSelectionEnabled = false
        listenData()
        setupTextFields()
        setupCellFactories()
        setupCellValueFactories()
        setupComboBox()
    }

    private fun listenData() {
        data.addListener(ListChangeListener { c ->
            while (c.next()) {
                mapItems()
                listenToItems()
            }
        })
    }

    private fun mapItems() {
        table.items = data.map { vesFxModelMapper.toObservable(it) }.toObservableList()
    }

    private fun listenToItems() {
        table.items.forEach { item ->
            item.resistivityApparentProperty().addListener { _, _, _ -> updateChart() }
            item.errorResistivityApparentProperty().addListener { _, _, _ -> updateChart() }
        }
    }

    private fun Number.fmt(): String = df.format(this)

    private fun setupComboBox() {
        errorFormula.items.setAll(ErrorFunctionType.entries)
        errorFormula.converter = object : StringConverter<ErrorFunctionType>() {
            override fun toString(value: ErrorFunctionType?): String? {
                return when (value) {
                    ErrorFunctionType.USING_VARIANCE -> uiProps["errorFunction.usingVariance"]
                    ErrorFunctionType.USING_TRUST_INTERVALS -> uiProps["errorFunction.usingTrustIntervals"]
                    null -> null
                }
            }

            override fun fromString(value: String?): ErrorFunctionType? = null

        }
        errorFormula.selectionModel.selectedItemProperty().addListener { _, _, newValue: ErrorFunctionType ->
            errorFunctionVersion = newValue
        }
        errorFormula.selectionModel.selectFirst()
    }

    private fun setupCellValueFactories() {
        ab2Col.cellValueFactory = Callback { f ->
            createStringBinding(
                {
                    val (min, max) = measureError(
                        f.value.ab2,
                        distAErrorTf.textFormatter.value as Double,
                        distBErrorTf.textFormatter.value as Double
                    )
                    "${min.fmt()} $LEQ_SIGN ${f.value.ab2.fmt()} $LEQ_SIGN ${max.fmt()}"
                },
                f.value.ab2Property(),
                distAErrorTf.textFormatter.valueProperty(),
                distBErrorTf.textFormatter.valueProperty()
            )
        }
        mn2Col.cellValueFactory = Callback { f ->
            createStringBinding(
                {
                    val (min, max) = measureError(
                        f.value.mn2,
                        distAErrorTf.textFormatter.value as Double,
                        distBErrorTf.textFormatter.value as Double
                    )
                    "${min.fmt()} $LEQ_SIGN ${f.value.mn2.fmt()} $LEQ_SIGN ${max.fmt()}"
                },
                f.value.mn2Property(),
                distAErrorTf.textFormatter.valueProperty(),
                distBErrorTf.textFormatter.valueProperty()
            )
        }
        kCol.cellValueFactory = Callback { f ->
            createStringBinding(
                {
                    val (k, min, max) = kWithError(
                        f.value.ab2,
                        f.value.mn2,
                        distAErrorTf.textFormatter.value as Double,
                        distBErrorTf.textFormatter.value as Double
                    )
                    "${min.fmt()} $LEQ_SIGN ${k.fmt()} $LEQ_SIGN ${max.fmt()}"
                },
                f.value.ab2Property(),
                f.value.mn2Property(),
                distAErrorTf.textFormatter.valueProperty(),
                distBErrorTf.textFormatter.valueProperty()
            )
        }
        amperageCol.cellValueFactory = Callback { f ->
            createStringBinding(
                {
                    val (min, max) = measureError(
                        f.value.amperage,
                        iAErrorTf.textFormatter.value as Double,
                        iBErrorTf.textFormatter.value as Double
                    )
                    "${min.fmt()} $LEQ_SIGN ${f.value.amperage.fmt()} $LEQ_SIGN ${max.fmt()}"
                },
                f.value.amperageProperty(),
                iAErrorTf.textFormatter.valueProperty(),
                iBErrorTf.textFormatter.valueProperty()
            )
        }
        voltageCol.cellValueFactory = Callback { f ->
            createStringBinding(
                {
                    val (min, max) = measureError(
                        f.value.voltage,
                        uAErrorTf.textFormatter.value as Double,
                        uBErrorTf.textFormatter.value as Double
                    )
                    "${min.fmt()} $LEQ_SIGN ${f.value.voltage.fmt()} $LEQ_SIGN ${max.fmt()}"
                },
                f.value.voltageProperty(),
                uAErrorTf.textFormatter.valueProperty(),
                uBErrorTf.textFormatter.valueProperty()
            )
        }
        resCol.cellValueFactory = Callback { f ->
            createStringBinding(
                {
                    val (rhoA, min, max, _, _) = resAppWithError(vesFxModelMapper.toModel(f.value))
                    "${min.fmt()} $LEQ_SIGN ${rhoA.fmt()} $LEQ_SIGN ${max.fmt()}"
                },
                f.value.ab2Property(),
                distAErrorTf.textFormatter.valueProperty(),
                distBErrorTf.textFormatter.valueProperty(),
                uAErrorTf.textFormatter.valueProperty(),
                uBErrorTf.textFormatter.valueProperty(),
                iAErrorTf.textFormatter.valueProperty(),
                iBErrorTf.textFormatter.valueProperty(),
                errorFormula.selectionModel.selectedItemProperty()
            )
        }
        resAvgCol.cellValueFactory = Callback { f ->
            createStringBinding(
                {
                    val (_, _, _, avg, _) = resAppWithError(vesFxModelMapper.toModel(f.value))
                    f.value.resistivityApparent = avg
                    avg.fmt()
                },
                f.value.ab2Property(),
                distAErrorTf.textFormatter.valueProperty(),
                distBErrorTf.textFormatter.valueProperty(),
                uAErrorTf.textFormatter.valueProperty(),
                uBErrorTf.textFormatter.valueProperty(),
                iAErrorTf.textFormatter.valueProperty(),
                iBErrorTf.textFormatter.valueProperty(),
                errorFormula.selectionModel.selectedItemProperty()
            )
        }
        errorResCol.cellValueFactory = Callback { f ->
            createStringBinding(
                {
                    val (_, _, _, _, error) = resAppWithError(vesFxModelMapper.toModel(f.value))
                    f.value.errorResistivityApparent = error
                    error.fmt()
                },
                f.value.ab2Property(),
                distAErrorTf.textFormatter.valueProperty(),
                distBErrorTf.textFormatter.valueProperty(),
                uAErrorTf.textFormatter.valueProperty(),
                uBErrorTf.textFormatter.valueProperty(),
                iAErrorTf.textFormatter.valueProperty(),
                iBErrorTf.textFormatter.valueProperty(),
                errorFormula.selectionModel.selectedItemProperty()
            )
        }
    }

    private fun resAppWithError(data: ExperimentalData): ValueMinMaxAvgError {
        val k = kWithError(
            data.ab2,
            data.mn2,
            distAErrorTf.textFormatter.value as Double,
            distBErrorTf.textFormatter.value as Double
        )
        val u = measureError(
            data.voltage,
            uAErrorTf.textFormatter.value as Double,
            uBErrorTf.textFormatter.value as Double
        ).withValue(data.voltage)
        val i = measureError(
            data.amperage,
            iAErrorTf.textFormatter.value as Double,
            iBErrorTf.textFormatter.value as Double
        ).withValue(data.amperage)
        return resistivityApparentWithError(errorFunctionVersion == ErrorFunctionType.USING_VARIANCE, k, u, i)
    }

    private fun resAppErrorForDist(data: ExperimentalData) =
        resistivityApparentErrorForDistance(
            errorFunctionVersion == ErrorFunctionType.USING_VARIANCE,
            kWithError(
                data.ab2,
                data.mn2,
                distAErrorTf.textFormatter.value as Double,
                distBErrorTf.textFormatter.value as Double
            ),
            data.voltage,
            data.amperage
        )

    private fun resAppErrorForVolt(data: ExperimentalData) =
        resistivityApparentErrorForVoltage(
            errorFunctionVersion == ErrorFunctionType.USING_VARIANCE,
            measureError(
                data.voltage,
                uAErrorTf.textFormatter.value as Double,
                uBErrorTf.textFormatter.value as Double
            ).withValue(data.voltage),
            data.ab2,
            data.mn2,
            data.amperage
        )

    private fun resAppErrorForAmp(data: ExperimentalData) =
        resistivityApparentErrorForAmperage(
            errorFunctionVersion == ErrorFunctionType.USING_VARIANCE,
            measureError(
                data.amperage,
                iAErrorTf.textFormatter.value as Double,
                iBErrorTf.textFormatter.value as Double
            ).withValue(data.amperage),
            data.ab2,
            data.mn2,
            data.voltage
        )


    private fun updateChart() {
        val errorForAll = Series(data.map {
            XYChart.Data(it.ab2 as Number, resAppWithError(it).error as Number)
        }.toObservableList())
        val errorForDist = Series(data.map {
            XYChart.Data(it.ab2 as Number, resAppErrorForDist(it) as Number)
        }.toObservableList())
        val errorForVolt = Series(data.map {
            XYChart.Data(it.ab2 as Number, resAppErrorForVolt(it) as Number)
        }.toObservableList())
        val errorForAmp = Series(data.map {
            XYChart.Data(it.ab2 as Number, resAppErrorForAmp(it) as Number)
        }.toObservableList())
        chart.data.clear()
        chart.data.addAll(
            errorForAll,
            errorForDist,
            errorForVolt,
            errorForAmp
        )
        with(chart) {
            data[0].name = "Общая"
            data[1].name = "Расстояние"
            data[2].name = "Напряжение"
            data[3].name = "Ток"
        }
    }

    private fun setupCellFactories() {
        indexCol.cellFactory = indexCellFactory()
    }

    private fun setupTextFields() {
        distAErrorTf.textFormatter = TextFormatter(DoubleValidationConverter(preciseFormat), DEFAULT_DIST_A_ERROR)
        distBErrorTf.textFormatter = TextFormatter(DoubleValidationConverter(preciseFormat), DEFAULT_DIST_B_ERROR)
        uAErrorTf.textFormatter = TextFormatter(DoubleValidationConverter(preciseFormat), DEFAULT_U_A_ERROR)
        uBErrorTf.textFormatter = TextFormatter(DoubleValidationConverter(preciseFormat), DEFAULT_U_B_ERROR)
        iAErrorTf.textFormatter = TextFormatter(DoubleValidationConverter(preciseFormat), DEFAULT_I_A_ERROR)
        iBErrorTf.textFormatter = TextFormatter(DoubleValidationConverter(preciseFormat), DEFAULT_I_B_ERROR)
    }

    @FXML
    private fun apply() {
        commitChanges()
        root.close()
    }

    private fun commitChanges() {
        val modifiedData = observablePicket.get().sortedExperimentalData.toMutableList().also { list ->
            list.replaceAll {
                if (it in data) it.copy(
                    resistivityApparent = table.items[data.indexOf(it)].resistivityApparent,
                    errorResistivityApparent = table.items[data.indexOf(it)].errorResistivityApparent
                ) else it
            }
        }
        historyManager.snapshotAfter {
            observableSection.pickets[picketIndexProp.value] =
                observablePicket.get().copy(experimentalData = modifiedData)
        }
    }
}