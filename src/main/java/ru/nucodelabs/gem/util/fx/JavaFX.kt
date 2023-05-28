package ru.nucodelabs.gem.util.fx

import javafx.stage.Stage

object JavaFX {
    val currentWindow: Stage
        get() = Stage.getWindows().find { it.isFocused } as Stage
}