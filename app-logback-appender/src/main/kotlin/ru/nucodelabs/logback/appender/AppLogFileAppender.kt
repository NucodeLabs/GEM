package ru.nucodelabs.logback.appender

import ch.qos.logback.core.FileAppender

class AppLogFileAppender<E> : FileAppender<E>() {

    var appName: String? = null
    var logFileName: String = DEFAULT_LOG_FILE

    override fun start() {
        if (this.appName == null) {
            addError("AppName property not set for appender named [${name}]")
            return
        }
        this.file = appName?.let {
            appLogsDir(it).resolve(logFileName).toAbsolutePath().toString()
        }
        addInfo("Application logs location: $file")

        super.start()
    }
}
