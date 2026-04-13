package ru.nucodelabs.geo.ves

class InvalidPropertiesException(
    val errors: List<InvalidPropertyValue>
) : Exception(errors.joinToString())

data class InvalidPropertyValue(
    val property: String,
    val message: String,
    val value: Any?
)