package ru.nucodelabs.gem.fxmodel.exception

import jakarta.validation.ConstraintViolation

class DataValidationException(
    message: String?,
    val violations: Set<ConstraintViolation<*>>
) : RuntimeException(message)