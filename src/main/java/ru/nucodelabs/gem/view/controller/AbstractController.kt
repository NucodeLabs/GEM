package ru.nucodelabs.gem.view.controller

import javafx.fxml.Initializable
import javafx.stage.Stage

/**
 * Контроллер, который не только инициализируется FXML-загрузчиком, но и знает как добыть свою сцену
 */
@Deprecated(
    message = "Use AbstractViewController",
    replaceWith = ReplaceWith(
        "AbstractViewController()",
        "ru.nucodelabs.kfx.core.AbstractViewController"
    )
)
abstract class AbstractController : Initializable {
    protected abstract val stage: Stage?
}