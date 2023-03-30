package ru.nucodelabs.geo.map

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.lang.Math.toRadians
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

internal class CoordinatesKtTest {

    private val distValue = 480.0
    private val eps = 1e-6

    @Test
    fun xFromCenter() {
        val a = 285.0
        val ref = distValue * cos(toRadians(15.0)) * -1
        assertTrue(abs(ref - ru.nucodelabs.geo.anisotropy.calc.map.xFromCenter(distValue, a)) < eps)
    }

    @Test
    fun yFromCenter() {
        val a = 285.0
        val ref = distValue * sin(toRadians(15.0))
        assertTrue(abs(ref - ru.nucodelabs.geo.anisotropy.calc.map.yFromCenter(distValue, a)) < eps)
    }
}