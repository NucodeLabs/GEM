package ru.nucodelabs.util

data class Point(
    val x: Double,
    val y: Double,
    val z: Double
)

fun Point(x: Double, y: Double) = Point(x, y, .0)