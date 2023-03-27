package ru.nucodelabs.gem.util.std

abstract class AutoEquals {
    private var includeInEquals: List<() -> Any?> = listOf()

    protected fun includeInEquals(vararg getters: () -> Any?) {
        includeInEquals = getters.toList()
    }

    private fun equalsPropertiesValues(): EqualsProperties {
        return EqualsProperties(includeInEquals.map { it() })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AutoEquals

        if (equalsPropertiesValues() != other.equalsPropertiesValues()) return false

        return true
    }

    override fun hashCode(): Int {
        return includeInEquals.hashCode()
    }
}

private data class EqualsProperties(
    val properties: List<Any?>
)
