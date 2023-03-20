package ru.nucodelabs.gem.config

/**
 * Save values annotated with @State(identifier)
 */
@Target(AnnotationTarget.FUNCTION)
annotation class SaveState

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
annotation class State(val value: String)

