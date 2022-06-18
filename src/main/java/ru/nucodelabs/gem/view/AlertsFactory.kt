package ru.nucodelabs.gem.view

import jakarta.validation.ConstraintViolation
import javafx.scene.control.Alert
import javafx.stage.Stage
import java.util.*
import javax.inject.Inject

class AlertsFactory @Inject constructor(private val uiProperties: ResourceBundle) {

    @JvmOverloads
    fun simpleExceptionAlert(e: Exception, owner: Stage? = null): Alert =
        Alert(Alert.AlertType.ERROR, e.message).apply {
            title = uiProperties.getString("error")
            initOwner(owner)
        }

    fun unsatisfiedLinkErrorAlert(e: UnsatisfiedLinkError, owner: Stage?): Alert =
        Alert(Alert.AlertType.ERROR, e.message).apply {
            title = uiProperties.getString("noLib")
            headerText = uiProperties.getString("unableToDrawChart")
            initOwner(owner)
        }


    fun incorrectFileAlert(e: Exception, owner: Stage?): Alert =
        simpleExceptionAlert(e, owner).apply {
            headerText = uiProperties.getString("fileError")
        }


    fun unsafeDataAlert(picketName: String, owner: Stage?): Alert =
        Alert(Alert.AlertType.WARNING).apply {
            title = uiProperties.getString("compatibilityMode")
            headerText = picketName + " - " + uiProperties.getString("EXPSTTMismatch")
            contentText = uiProperties.getString("minimalDataWillBeDisplayed")
            initOwner(owner)
        }

    fun violationsAlert(violations: Set<ConstraintViolation<*>>, owner: Stage?): Alert {
        val message = violations.joinToString("\n") { it.message }
        return Alert(Alert.AlertType.ERROR, message).apply {
            initOwner(owner)
        }
    }
}