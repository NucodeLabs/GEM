package ru.nucodelabs.gem.app

import javafx.application.Platform
import ru.nucodelabs.gem.config.slf4j
import ru.nucodelabs.gem.view.AlertsFactory
import java.lang.Thread.UncaughtExceptionHandler

class UncaughtExceptionHandler(
    private val alertsFactory: AlertsFactory,
) : UncaughtExceptionHandler {

    val log = slf4j()

    override fun uncaughtException(t: Thread, e: Throwable) {
        log.error("Uncaught exception", e)
        if (Platform.isFxApplicationThread()) {
            if (e is UnsatisfiedLinkError) {
                alertsFactory.unsatisfiedLinkErrorAlert(e).show()
            } else {
                alertsFactory.uncaughtExceptionAlert(e).show()
            }
        }
    }
}