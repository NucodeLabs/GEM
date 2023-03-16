package ru.nucodelabs.gem.app.snapshot

class StateHolder {
    private val states: MutableMap<String, Any> = mutableMapOf()

    fun getState(identifier: String) = states[identifier]

    fun putState(identifier: String, state: Any) {
        states[identifier] = state
    }

    fun resetState(identifier: String) {
        states -= identifier
    }
}