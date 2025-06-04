package ru.nucodelabs.gem.app

import javafx.application.Application

/**
 * Main entry point launching JavaFX application.
 */
object StartGemApplication {
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            Application.launch(GemApplication::class.java, *args)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
