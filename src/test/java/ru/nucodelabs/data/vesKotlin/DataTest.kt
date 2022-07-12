package ru.nucodelabs.data.vesKotlin

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.Validation
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DataTest {

    private val objectMapper = ObjectMapper()
    private val validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun validation() {
        val modelLayer = ModelLayer(-12.0, 13.0, isPowerFixed = false, isResistanceFixed = false)

        assertTrue(validator.validate(modelLayer).size == 1)
    }

    @Test
    fun test() {
        val range1 = 1.0..5.1
        val range2 = 1.0..5.1

        assertTrue(range1 == range2)
    }
}