package ru.nucodelabs.gem.app

import org.slf4j.LoggerFactory
import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.kfx.core.OS
import java.io.File
import java.lang.Thread.UncaughtExceptionHandler
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ExceptionHandler(
    private val alertsFactory: AlertsFactory,
    private val printTrace: Boolean
) : UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        LoggerFactory.getLogger(ExceptionHandler::class.java)
            .error("Uncaught exception", e)
        handleUnexpectedException(e)
    }

    private fun handleUnexpectedException(e: Throwable) {
        alertsFactory.uncaughtExceptionAlert(e).show()
        if (printTrace) {
            e.printStackTrace()
        } else {
            val log =
                File(
                    "err-trace_${OS.osNameClassifier}_${
                        LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    }_${LocalTime.now()}.txt"
                ).also { it.createNewFile() }
            log.writeText(e.stackTraceToString())
        }
    }
}