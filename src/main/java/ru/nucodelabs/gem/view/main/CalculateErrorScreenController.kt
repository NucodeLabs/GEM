package ru.nucodelabs.gem.view.main

import com.google.inject.name.Named
import javafx.beans.binding.Bindings
import javafx.beans.binding.Bindings.createStringBinding
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.stage.Stage
import javafx.util.Callback
import ru.nucodelabs.algorithms.error.kWithError
import ru.nucodelabs.algorithms.error.measureError
import ru.nucodelabs.algorithms.error.resistanceApparentWithError
import ru.nucodelabs.algorithms.error.withValue
import ru.nucodelabs.data.fx.ObservableExperimentalData
import ru.nucodelabs.data.fx.toObservable
import ru.nucodelabs.gem.extensions.fx.DoubleValidationConverter
import ru.nucodelabs.gem.extensions.fx.observableListOf
import ru.nucodelabs.gem.extensions.std.toNumber
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.tables.indexCellFactory
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

const val DEFAULT_DIST_A_ERROR = 5e-3
const val DEFAULT_DIST_B_ERROR = 1e-3

const val DEFAULT_U_A_ERROR = 5e-3
const val DEFAULT_U_B_ERROR = 1e-6

const val DEFAULT_I_A_ERROR = 5e-3
const val DEFAULT_I_B_ERROR = 1e-6

const val LEQ_S = '≤'

class CalculateErrorScreenController @Inject constructor(
    @Named("Precise") private val preciseFormat: DecimalFormat,
    private val df: DecimalFormat
) : AbstractController() {
    @FXML
    private lateinit var errorResistanceCol: TableColumn<ObservableExperimentalData, String>

    @FXML
    private lateinit var rhoACol: TableColumn<ObservableExperimentalData, String>

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

    // SHARED ITEMS
    val data = observableListOf<ObservableExperimentalData>()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        table.selectionModel.isCellSelectionEnabled = false
        Bindings.bindContent(table.items, data)
        setupTextFields()
        setupCellFactories()
        setupCellValueFactories()
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
        rhoACol.cellValueFactory = Callback { f ->
            createStringBinding(
                {
                    val (rhoA, min, max, avg, _) = resistanceApparentWithError(
                        kWithError(
                            f.value.ab2,
                            f.value.mn2,
                            distAErrorTf.textFormatter.value as Double,
                            distBErrorTf.textFormatter.value as Double
                        ),
                        measureError(
                            f.value.voltage,
                            uAErrorTf.textFormatter.value as Double,
                            uBErrorTf.textFormatter.value as Double
                        ).withValue(f.value.voltage),
                        measureError(
                            f.value.amperage,
                            iAErrorTf.textFormatter.value as Double,
                            iBErrorTf.textFormatter.value as Double
                        ).withValue(f.value.amperage)
                    )
                    "${min.fmt()} $LEQ_S ${rhoA.fmt()} $LEQ_S ${max.fmt()}, avg = ${avg.fmt()}"
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
                    val (_, _, _, _, error) = resistanceApparentWithError(
                        kWithError(
                            f.value.ab2,
                            f.value.mn2,
                            distAErrorTf.textFormatter.value as Double,
                            distBErrorTf.textFormatter.value as Double
                        ),
                        measureError(
                            f.value.voltage,
                            uAErrorTf.textFormatter.value as Double,
                            uBErrorTf.textFormatter.value as Double
                        ).withValue(f.value.voltage),
                        measureError(
                            f.value.amperage,
                            iAErrorTf.textFormatter.value as Double,
                            iBErrorTf.textFormatter.value as Double
                        ).withValue(f.value.amperage)
                    )
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
        val items = data.map { it.toExperimentalData() }.toMutableList()
        for (i in items.indices) {
            items[i] = items[i].copy(
                errorResistanceApparent = errorResistanceCol.getCellData(i).toNumber(df).toDouble()
            )
        }
        data.setAll(items.map { it.toObservable() })
        root.close()
    }
}