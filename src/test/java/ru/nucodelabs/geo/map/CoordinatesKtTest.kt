package ru.nucodelabs.geo.map

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin

internal class CoordinatesKtTest {

    private val distValue = 480.0

    @Test
    fun xFromCenter() {
        val a = 285.0
        val ref = distValue * cos(toRadians(15.0)) * -1
        assertEquals(ref, xFromCenter(distValue, a))
    }

    @Test
    fun yFromCenter() {
        val a = 285.0
        val ref = distValue * sin(toRadians(15.0))
        assertEquals(ref, yFromCenter(distValue, a))
    }
}