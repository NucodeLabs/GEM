package ru.nucodelabs.gem.view.controller

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.stage.Stage
import java.net.URL
import java.util.*

/**
 * Контроллер, который не только инициализируется FXML-загрузчиком, но и знает как добыть свою сцену
 */
abstract class AbstractController : Initializable {
    protected abstract val stage: Stage?

    @FXML
    protected var fxScriptInit: Runnable = Runnable {}

    override fun initialize(location: URL, resources: ResourceBundle) {
        fxScriptInit()
    }

    protected fun fxScriptInit() = fxScriptInit.run()
}