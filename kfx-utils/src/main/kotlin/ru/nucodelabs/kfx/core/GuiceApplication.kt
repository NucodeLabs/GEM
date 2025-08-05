package ru.nucodelabs.kfx.core

import com.google.inject.Guice
import com.google.inject.Module
import javafx.application.Application

/**
 * JavaFX Application inherited class.
 * Constructor takes Guice configuration modules and then
 * you can use injector via `guiceInjector` property.
 * Also it injects all fields in application itself using this injector.
 */
abstract class GuiceApplication(
    vararg modules: Module
) : Application() {
    protected val guiceInjector = Guice.createInjector(*modules)
    override fun init() {
        guiceInjector.injectMembers(this)
    }
}