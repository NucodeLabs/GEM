package ru.nucodelabs.gem.view

import javafx.fxml.Initializable
import javafx.stage.Stage

/**
 * Контроллер, который не только инициализируется FXML-загрузчиком, но и знает как добыть свою сцену
 */
abstract class AbstractController : Initializable {
    protected abstract val stage: Stage?
}