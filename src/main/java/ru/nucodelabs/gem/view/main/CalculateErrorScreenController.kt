package ru.nucodelabs.gem.view.main

import com.google.inject.name.Named
import javafx.beans.binding.Bindings.createStringBinding
import javafx.beans.property.IntegerProperty
import javafx.beans.value.ObservableObjectValue
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.stage.Stage
import javafx.util.Callback
import ru.nucodelabs.geo.ves.calc.error.*
import ru.nucodelabs.gem.fxmodel.ObservableExperimentalData
import ru.nucodelabs.gem.fxmodel.ObservableSection
import ru.nucodelabs.gem.fxmodel.toObservable
import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.Picket
import ru.nucodelabs.geo.ves.Section
import ru.nucodelabs.gem.app.snapshot.HistoryManager
import ru.nucodelabs.gem.util.fx.DoubleValidationConverter
import ru.nucodelabs.gem.util.fx.observableListOf
import ru.nucodelabs.gem.util.fx.toObservableList
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.tables.indexCellFactory
import ru.nucodelabs.geo.ves.calc.error.*
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

const val DEFAULT_DIST_A_ERROR = 0.5
const val DEFAULT_DIST_B_ERROR = 1e-3 // m

const val DEFAULT_U_A_ERROR = 0.5
const val DEFAULT_U_B_ERROR = 0.5 // mV

const val DEFAULT_I_A_ERROR = 0.5
const val DEFAULT_I_B_ERROR = 0.5 // mA

const val LEQ_S = '≤'

class CalculateErrorScreenController @Inject constructor(
    @Named("Precise") private val preciseFormat: DecimalFormat,
    private val df: DecimalFormat,
    private val observableSection: ObservableSection,
    private val historyManager: HistoryManager<Section>,
    private val observablePicket: ObservableObjectValue<Picket>,
    private val picketIndexProp: IntegerProperty
) : AbstractController() {
    @FXML
    private lateinit var resAvgCol: TableColumn<ObservableExperimentalData, String>

    @FXML
    private lateinit var chart: LineChart<Number, Number>

    @FXML
    private lateinit var errorResistanceCol: TableColumn<ObservableExperimentalData, String>

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

    override fun initialize(location: URL, resources: ResourceBundle) {
        table.selectionModel.isCellSelectionEnabled = false
        listenData()
        setupTextFields()
        setupCellFactories()
        setupCellValueFactories()
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
        table.items = data.map { it.toObservable() }.toObservableList()
    }

    private fun listenToItems() {
        table.items.forEach { item ->
            item.resistanceApparentProperty().addListener { _, _, _ -> updateChart() }
            item.errorResistanceApparentProperty().addListener { _, _, _ -> updateChart() }
        }
    }

    private fun Number.fmt(): String = df.format(this)

    private fun setupCellValueFactories() {
        ab2Col.cellValueFactory = Callback { f ->
            createStringBinding(
                {
                    val (min, max) = measureError(
                        f.value.ab2,
                        distAErrorTf.textFormatter.value as Double,
                        distBErrorTf.textFormatter.value as Double
                    )
                    "${min.fmt()} $LEQ_S ${f.value.ab2.fmt()} $LEQ_S ${max.fmt()}"
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
                    "${min.fmt()} $LEQ_S ${f.value.mn2.fmt()} $LEQ_S ${max.fmt()}"
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
                    "${min.fmt()} $LEQ_S ${k.fmt()} $LEQ_S ${max.fmt()}"
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
                    "${min.fmt()} $LEQ_S ${f.value.amperage.fmt()} $LEQ_S ${max.fmt()}"
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
                    "${min.fmt()} $LEQ_S ${f.value.voltage.fmt()} $LEQ_S ${max.fmt()}"
                },
                f.value.voltageProperty(),
                uAErrorTf.textFormatter.valueProperty(),
                uBErrorTf.textFormatter.valueProperty()
            )
        }
        resCol.cellValueFactory = Callback { f ->
            createStringBinding(
                {
                    val (rhoA, min, max, _, _) = resAppWithError(f.value.toExperimentalData())
                    "${min.fmt()} $LEQ_S ${rhoA.fmt()} $LEQ_S ${max.fmt()}"
                },
                f.value.ab2Property(),
                distAErrorTf.textFormatter.valueProperty(),
                distBErrorTf.textFormatter.valueProperty(),
                uAErrorTf.textFormatter.valueProperty(),
                uBErrorTf.textFormatter.valueProperty(),
                iAErrorTf.textFormatter.valueProperty(),
                iBErrorTf.textFormatter.valueProperty()
            )
        }
        resAvgCol.cellValueFactory = Callback { f ->
            createStringBinding(
                {
                    val (_, _, _, avg, _) = resAppWithError(f.value.toExperimentalData())
                    f.value.resistanceApparent = avg
                    avg.fmt()
                },
                f.value.ab2Property(),
                distAErrorTf.textFormatter.valueProperty(),
                distBErrorTf.textFormatter.valueProperty(),
                uAErrorTf.textFormatter.valueProperty(),
                uBErrorTf.textFormatter.valueProperty(),
                iAErrorTf.textFormatter.valueProperty(),
                iBErrorTf.textFormatter.valueProperty()
            )
        }
        errorResistanceCol.cellValueFactory = Callback { f ->
            createStringBinding(
                {
                    val (_, _, _, _, error) = resAppWithError(f.value.toExperimentalData())
                    f.value.errorResistanceApparent = error
                    error.fmt()
                },
                f.value.ab2Property(),
                distAErrorTf.textFormatter.valueProperty(),
                distBErrorTf.textFormatter.valueProperty(),
                uAErrorTf.textFormatter.valueProperty(),
                uBErrorTf.textFormatter.valueProperty(),
                iAErrorTf.textFormatter.valueProperty(),
                iBErrorTf.textFormatter.valueProperty()
            )
        }
    }

    private fun resAppWithError(data: ExperimentalData) =
        resistanceApparentWithError(
            kWithError(
                data.ab2,
                data.mn2,
                distAErrorTf.textFormatter.value as Double,
                distBErrorTf.textFormatter.value as Double
            ),
            measureError(
                data.voltage,
                uAErrorTf.textFormatter.value as Double,
                uBErrorTf.textFormatter.value as Double
            ).withValue(data.voltage),
            measureError(
                data.amperage,
                iAErrorTf.textFormatter.value as Double,
                iBErrorTf.textFormatter.value as Double
            ).withValue(data.amperage)
        )

    private fun resAppErrorForDist(data: ExperimentalData) =
        resistanceApparentErrorForDistance(
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
        resistanceApparentErrorForVoltage(
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
        resistanceApparentErrorForAmperage(
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
                    resistanceApparent = table.items[data.indexOf(it)].resistanceApparent,
                    errorResistanceApparent = table.items[data.indexOf(it)].errorResistanceApparent
                ) else it
            }
        }
        historyManager.snapshotAfter {
            observableSection.pickets[picketIndexProp.value] =
                observablePicket.get().copy(experimentalData = modifiedData)
        }
    }
}