package ru.nucodelabs.gem.view.controller

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.stage.Stage
import java.net.URL
import java.util.*

/**
 * Контроллер, который не только инициализируется FXML-загрузчиком, но и знает как добыть свою сцену
 */
@Deprecated(
    message = "Улучшенная версия вынесена в отдельной библиотеке https://github.com/lilvadim/kfx-utils",
    replaceWith = ReplaceWith(
        "AbstractViewController<T>()",
        "ru.nucodelabs.kfx.core.AbstractViewController"
    )
)
abstract class AbstractController : Initializable {
    protected abstract val stage: Stage?

    @FXML
    protected var fxScriptInit: Runnable = Runnable {}

    override fun initialize(location: URL, resources: ResourceBundle) {
        fxScriptInit()
    }

    protected fun fxScriptInit() = fxScriptInit.run()
}