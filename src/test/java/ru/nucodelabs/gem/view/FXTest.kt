package ru.nucodelabs.gem.view

import javafx.application.Platform
import org.junit.jupiter.api.BeforeAll

abstract class FXTest {
    companion object JfxRuntimeInitializer {
        @BeforeAll
        @JvmStatic
        fun initJfxRunTime() {
            try {
                Platform.startup {}
            } catch (_: Exception) {
            }
        }
    }
}