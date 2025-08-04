package ru.nucodelabs.gem.view

import jakarta.inject.Inject
import jakarta.validation.ConstraintViolation
import javafx.scene.control.Alert
import javafx.stage.Stage
import ru.nucodelabs.gem.util.fx.JavaFX
import ru.nucodelabs.gem.util.fx.get
import java.util.*

class AlertsFactory @Inject constructor(private val uiProperties: ResourceBundle) {

    fun simpleAlert(
        title: String = uiProperties["error"],
        headerText: String = "",
        text: String,
        owner: Stage? = JavaFX.currentWindow
    ) =
        Alert(Alert.AlertType.ERROR, text).apply {
            this.title = title
            this.headerText = headerText
            initOwner(owner)
        }

    fun uncaughtExceptionAlert(e: Throwable) = Alert(Alert.AlertType.ERROR, e.message).apply {
        title = uiProperties["error"]
        initOwner(JavaFX.currentWindow)
        headerText = "Сохраните важные данные и перезапустите программу"
    }

    @JvmOverloads
    fun simpleExceptionAlert(e: Throwable, owner: Stage? = JavaFX.currentWindow): Alert =
        Alert(Alert.AlertType.ERROR, e.message).apply {
            title = uiProperties["error"]
            headerText = title
            initOwner(owner)
        }

    @JvmOverloads
    fun unsatisfiedLinkErrorAlert(e: UnsatisfiedLinkError, owner: Stage? = JavaFX.currentWindow): Alert =
        Alert(Alert.AlertType.ERROR, e.message).apply {
            title = uiProperties["noLib"]
            headerText = uiProperties["unableToDrawChart"]
            initOwner(owner)
        }


    @JvmOverloads
    fun incorrectFileAlert(e: Exception, owner: Stage? = JavaFX.currentWindow): Alert =
        simpleExceptionAlert(e, owner).apply {
            headerText = uiProperties["fileError"]
        }

    @JvmOverloads
    fun violationsAlert(violations: Set<ConstraintViolation<*>>, owner: Stage? = JavaFX.currentWindow): Alert {
        val message = violations.joinToString("\n") { it.message }
        return Alert(Alert.AlertType.ERROR, message).apply {
            initOwner(owner)
        }
    }
}