package ru.nucodelabs.gem.app

import ru.nucodelabs.gem.app.io.slf4j
import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.kfx.core.OS
import java.io.File
import java.lang.Thread.UncaughtExceptionHandler
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UncaughtExceptionHandler(
    private val alertsFactory: AlertsFactory,
    private val writeToFileDisabled: Boolean
) : UncaughtExceptionHandler {

    val log = slf4j(this)

    override fun uncaughtException(t: Thread, e: Throwable) {
        log.error("Uncaught exception", e)
        handleUnexpectedException(e)
    }

    private fun handleUnexpectedException(e: Throwable) {
        alertsFactory.uncaughtExceptionAlert(e).show()
        if (!writeToFileDisabled) {
            val dateNow = LocalDateTime.now()
            val formattedDate = dateNow
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            val timestamp = java.time.Instant.now().toEpochMilli()
            val traceFile =
                File("err-trace_${OS.osNameClassifier}_${formattedDate}_${timestamp}.txt").also {
                    it.createNewFile()
                }
            traceFile.writeText(e.stackTraceToString())
        }
    }
}