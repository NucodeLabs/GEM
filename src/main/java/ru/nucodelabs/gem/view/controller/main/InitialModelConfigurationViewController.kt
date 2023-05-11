package ru.nucodelabs.gem.view.controller.main

import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import ru.nucodelabs.gem.fxmodel.ves.app.ArbitraryInitialModelParameters
import ru.nucodelabs.gem.fxmodel.ves.app.VesFxAppModel
import ru.nucodelabs.gem.util.std.toDoubleOrNullBy
import ru.nucodelabs.gem.util.std.toIntOrNullBy
import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.kfx.core.AbstractViewController
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

class InitialModelConfigurationViewController @Inject constructor(
    private val decimalFormat: DecimalFormat,
    private val alertsFactory: AlertsFactory,
) : AbstractViewController<VBox>() {

    var parameters = VesFxAppModel.DEFAULT_INITIAL_MODEL_PARAMETERS

    @FXML
    private lateinit var maxLayersCountTextField: TextField

    @FXML
    private lateinit var minTargetFunctionValueTextField: TextField

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        minTargetFunctionValueTextField.text = decimalFormat.format(parameters.minTargetFunctionValue)
        maxLayersCountTextField.text = decimalFormat.format(parameters.maxLayersCount)
    }

    @FXML
    private fun apply() {
        val minTargetFunctionValue = minTargetFunctionValueTextField.text.toDoubleOrNullBy(decimalFormat)
        val maxLayersCount = maxLayersCountTextField.text.toIntOrNullBy(decimalFormat)

        if (minTargetFunctionValue == null || maxLayersCount == null) {
            alertsFactory.simpleAlert(text = "Неправильный ввод", owner = stage).show()
            return
        }

        parameters = ArbitraryInitialModelParameters(
            minTargetFunctionValue = minTargetFunctionValue,
            maxLayersCount = maxLayersCount
        )

        stage?.close()
    }
}