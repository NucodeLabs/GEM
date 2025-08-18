package ru.nucodelabs.gem.config

import ch.qos.logback.classic.BasicConfigurator
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.layout.TTLLLayout
import ch.qos.logback.classic.spi.Configurator
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.encoder.LayoutWrappingEncoder
import ch.qos.logback.core.spi.ContextAwareBase
import ru.nucodelabs.logback.appender.AppLogFileAppender

class GemLogbackConfigurator : ContextAwareBase(), Configurator {

    override fun configure(loggerContext: LoggerContext?): Configurator.ExecutionStatus? {
        if (System.getProperty("app.gem.log.stdout", "false").toBoolean()) {
            BasicConfigurator().also { it.context = loggerContext }.configure(loggerContext)
        }

        val appender = AppLogFileAppender<ILoggingEvent>().apply {
            context = loggerContext
            name = "APP_FILE"
            appName = "ru.nucodelabs.gem"
            logFileName = "last_session.log"
            isAppend = false
            encoder = LayoutWrappingEncoder<ILoggingEvent>().apply {
                context = loggerContext
                layout = TTLLLayout().apply { context = loggerContext }.also { it.start() }
            }.also { it.start() }
        }.also { it.start() }

        loggerContext?.getLogger(Logger.ROOT_LOGGER_NAME)?.let { root ->
            root.isAdditive = false
            root.level = Level.valueOf(System.getProperty("app.gem.log.level", "INFO"))
            root.addAppender(appender)
        }

        return Configurator.ExecutionStatus.DO_NOT_INVOKE_NEXT_IF_ANY
    }
}