package ru.nucodelabs.gem.view.control

import javafx.beans.property.StringProperty
import javafx.fxml.FXML
import javafx.scene.control.Label

class Placeholder : VBUserControl() {

    @FXML
    private lateinit var text: Label

    var textValue: String
        get() = text.text
        set(value) {
            text.text = value
        }

    fun textProperty(): StringProperty = text.textProperty()
}
