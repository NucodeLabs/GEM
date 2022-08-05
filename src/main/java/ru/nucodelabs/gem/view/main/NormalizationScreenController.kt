package ru.nucodelabs.gem.view.main

import javafx.fxml.FXML
import javafx.stage.Stage
import ru.nucodelabs.gem.view.AbstractController

class NormalizationScreenController : AbstractController() {
    @FXML
    private lateinit var root: Stage

    override val stage: Stage
        get() = root

}