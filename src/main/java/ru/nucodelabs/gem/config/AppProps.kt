package ru.nucodelabs.gem.config

object AppProps {
    const val APP_NAME = "gem"
    const val FULL_APP_NAME = "ru.nucodelabs.$APP_NAME"

    const val DEFAULT_LOG_LEVEL = "INFO"
    const val DEFAULT_LOG_STDOUT = false

    val logLevel: String = System.getProperty("app.gem.log.level", DEFAULT_LOG_LEVEL)
    val logStdout: Boolean = System.getProperty("app.gem.log.stdout", DEFAULT_LOG_STDOUT.toString()).toBoolean()
}