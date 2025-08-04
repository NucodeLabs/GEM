package ru.nucodelabs.gem.fxmodel.exception

import ru.nucodelabs.gem.util.fx.JavaFX
import ru.nucodelabs.gem.view.AlertsFactory

fun validation(alertsFactory: AlertsFactory, block: () -> Unit) {
    try {
        block()
    } catch (e: DataValidationException) {
        alertsFactory.violationsAlert(e.violations, JavaFX.currentWindow).show()
    }
}