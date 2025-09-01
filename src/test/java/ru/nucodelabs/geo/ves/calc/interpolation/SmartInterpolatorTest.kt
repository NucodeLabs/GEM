package ru.nucodelabs.geo.ves.calc.interpolation

import org.junit.jupiter.api.Test

internal class SmartInterpolatorTest {

    @Suppress("UNCHECKED_CAST")
    @Test
    fun build() {
        val smartInterpolator = SmartInterpolator(RBFSpatialInterpolator(), ApacheInterpolator2D())
        smartInterpolator.build(AnisotropyTestData.points)
    }
}