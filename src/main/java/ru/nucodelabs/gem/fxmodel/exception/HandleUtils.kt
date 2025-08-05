package ru.nucodelabs.gem.fxmodel.exception

import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.kfx.ext.currentWindow

fun validation(alertsFactory: AlertsFactory, block: () -> Unit) {
    try {
        block()
    } catch (e: DataValidationException) {
        alertsFactory.violationsAlert(e.violations, currentWindow()).show()
    }
}