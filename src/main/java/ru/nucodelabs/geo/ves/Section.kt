package ru.nucodelabs.geo.ves

/**
 * Разрез
 * @property pickets Список пикетов для данного разреза
 */
data class Section(
    val pickets: List<Picket> = listOf()
)
