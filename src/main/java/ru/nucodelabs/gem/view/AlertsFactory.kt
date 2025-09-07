package ru.nucodelabs.gem.view

import jakarta.inject.Inject
import jakarta.validation.ConstraintViolation
import javafx.scene.control.Alert
import javafx.stage.Stage
import ru.nucodelabs.gem.config.slf4j
import ru.nucodelabs.kfx.ext.currentWindow
import ru.nucodelabs.kfx.ext.get
import java.util.*

class AlertsFactory @Inject constructor(private val uiProperties: ResourceBundle) {

    val log = slf4j(this)

    fun simpleAlert(
        title: String = uiProperties["error"],
        headerText: String = "",
        text: String,
        owner: Stage? = currentWindow()
    ) =
        Alert(Alert.AlertType.ERROR, text).apply {
            this.title = title
            this.headerText = headerText
            initOwner(owner)
        }

    fun uncaughtExceptionAlert(e: Throwable) = Alert(Alert.AlertType.ERROR, e.message).apply {
        title = uiProperties["error"]
        initOwner(currentWindow())
        headerText = "Сохраните важные данные и перезапустите программу"
    }.also { log.warn("Uncaught exception alert", e) }

    @JvmOverloads
    fun simpleExceptionAlert(e: Throwable, owner: Stage? = currentWindow()): Alert =
        Alert(Alert.AlertType.ERROR, e.message).apply {
            title = uiProperties["error"]
            headerText = title
            initOwner(owner)
        }.also { log.warn("Simple exception alert", e) }

    @JvmOverloads
    fun unsatisfiedLinkErrorAlert(e: UnsatisfiedLinkError, owner: Stage? = currentWindow()): Alert =
        Alert(Alert.AlertType.ERROR, e.message).apply {
            title = uiProperties["noLib"]
            headerText = uiProperties["unableToDrawChart"]
            initOwner(owner)
        }.also { log.warn("Unsatisfied link error", e) }


    @JvmOverloads
    fun incorrectFileAlert(e: Exception, owner: Stage? = currentWindow()): Alert =
        simpleExceptionAlert(e, owner).apply {
            headerText = uiProperties["fileError"]
        }.also { log.warn("Incorrect file alert", e) }

    @JvmOverloads
    fun violationsAlert(violations: Set<ConstraintViolation<*>>, owner: Stage? = currentWindow()): Alert {
        val message = violations.joinToString("\n") { it.message }
        return Alert(Alert.AlertType.ERROR, message).apply {
            initOwner(owner)
        }.also { log.warn("Validation violations alert: $message") }
    }
}