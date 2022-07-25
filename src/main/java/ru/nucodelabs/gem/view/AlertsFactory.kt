package ru.nucodelabs.gem.view

import jakarta.validation.ConstraintViolation
import javafx.scene.control.Alert
import javafx.stage.Stage
import ru.nucodelabs.gem.extensions.fx.get
import java.util.*
import javax.inject.Inject

class AlertsFactory @Inject constructor(private val uiProperties: ResourceBundle) {

    fun uncaughtExceptionAlert(e: Throwable) = Alert(Alert.AlertType.ERROR, e.message).apply {
        title = uiProperties["error"]
        headerText = "Сохраните важные данные и перезапустите программу"
    }

    @JvmOverloads
    fun simpleExceptionAlert(e: Throwable, owner: Stage? = null): Alert =
        Alert(Alert.AlertType.ERROR, e.message).apply {
            title = uiProperties["error"]
            headerText = title
            initOwner(owner)
        }

    @JvmOverloads
    fun unsatisfiedLinkErrorAlert(e: UnsatisfiedLinkError, owner: Stage? = null): Alert =
        Alert(Alert.AlertType.ERROR, e.message).apply {
            title = uiProperties["noLib"]
            headerText = uiProperties["unableToDrawChart"]
            initOwner(owner)
        }


    @JvmOverloads
    fun incorrectFileAlert(e: Exception, owner: Stage? = null): Alert =
        simpleExceptionAlert(e, owner).apply {
            headerText = uiProperties["fileError"]
        }


    @JvmOverloads
    fun unsafeDataAlert(picketName: String, owner: Stage? = null): Alert =
        Alert(Alert.AlertType.WARNING).apply {
            title = uiProperties["compatibilityMode"]
            headerText = "$picketName - ${uiProperties["EXPSTTMismatch"]}"
            contentText = uiProperties["minimalDataWillBeDisplayed"]
            initOwner(owner)
        }

    @JvmOverloads
    fun violationsAlert(violations: Set<ConstraintViolation<*>>, owner: Stage? = null): Alert {
        val message = violations.joinToString("\n") { it.message }
        return Alert(Alert.AlertType.ERROR, message).apply {
            initOwner(owner)
        }
    }
}