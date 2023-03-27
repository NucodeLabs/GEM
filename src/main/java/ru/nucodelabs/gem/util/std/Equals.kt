package ru.nucodelabs.gem.util.std

abstract class AutoEquals {

    private var getterCallbacks: List<() -> Any?> = listOf()

    protected fun includeInEquals(vararg getters: () -> Any?) {
        getterCallbacks = getters.toList()
    }

    private fun equalsPropertiesValues(): List<Any?> {
        return getterCallbacks.map { it() }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AutoEquals

        if (equalsPropertiesValues() != other.equalsPropertiesValues()) return false

        return true
    }

    override fun hashCode(): Int {
        return equalsPropertiesValues().hashCode()
    }
}