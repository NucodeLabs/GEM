package ru.nucodelabs.gem.view.main

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import javafx.beans.property.IntegerProperty
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.stage.Stage
import ru.nucodelabs.data.fx.ObservableSection
import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.data.ves.Section
import ru.nucodelabs.gem.app.snapshot.HistoryManager
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.AlertsFactory
import java.net.URL
import java.util.*
import javax.inject.Inject

class AddExperimentalDataController @Inject constructor(
    private val objectMapper: ObjectMapper,
    private val alertsFactory: AlertsFactory,
    private val picketIndex: IntegerProperty,
    private val observableSection: ObservableSection,
    private val historyManager: HistoryManager<Section>
) : AbstractController() {
    @FXML
    private lateinit var amperageTextField: TextField

    @FXML
    private lateinit var errResAppTextField: TextField

    @FXML
    private lateinit var voltageTextField: TextField

    @FXML
    private lateinit var resAppTextField: TextField

    @FXML
    private lateinit var mn2TextField: TextField

    @FXML
    private lateinit var ab2TextField: TextField

    @FXML
    private lateinit var root: VBox

    override val stage: Stage?
        get() = root.scene.window as Stage?

    override fun initialize(location: URL?, resources: ResourceBundle?) {
    }

    fun createJson(
        ab2: String,
        mn2: String,
        amperage: String,
        voltage: String,
        errorResApp: String,
        resApp: String
    ): String {
        return ("{" +
                (if (ab2.isNotBlank()) "\"ab2\": $ab2," else "") +
                (if (mn2.isNotBlank()) "\"mn2\": $mn2," else "") +
                (if (amperage.isNotBlank()) "\"amperage\": $amperage," else "") +
                (if (voltage.isNotBlank()) "\"voltage\": $voltage," else "") +
                (if (errorResApp.isNotBlank()) "\"errorResistanceApparent\": $errorResApp," else "") +
                (if (resApp.isNotBlank()) "\"resistanceApparent\": $resApp," else "") +
                "}").let {
            val idx = it.lastIndexOf(",")
            it.removeRange(idx, idx + 1)
        }
    }

    @FXML
    private fun add() {
        val newExpDataItem = try {
            objectMapper.readValue<ExperimentalData>(
                createJson(
                    ab2 = ab2TextField.text,
                    mn2 = mn2TextField.text,
                    amperage = amperageTextField.text,
                    voltage = voltageTextField.text,
                    errorResApp = errResAppTextField.text,
                    resApp = resAppTextField.text
                )
            )
        } catch (e: Exception) {
            alertsFactory.simpleExceptionAlert(e, stage).show()
            return
        }

        historyManager.snapshotAfter {
            val picket = observableSection.pickets[picketIndex.value]
            val expData = picket.sortedExperimentalData
            observableSection.pickets[picketIndex.value] =
                picket.copy(experimentalData = expData.toMutableList().apply {
                    add(newExpDataItem)
                })
        }
    }
}