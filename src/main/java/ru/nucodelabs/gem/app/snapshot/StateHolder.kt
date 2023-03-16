package ru.nucodelabs.gem.app.snapshot

class StateHolder<I, S> {
    private val states: MutableMap<I, S> = mutableMapOf()

    fun getState(identifier: I) = states[identifier]

    fun putState(identifier: I, state: S) {
        states[identifier] = state
    }

    fun resetState(identifier: I) {
        states -= identifier
    }
}