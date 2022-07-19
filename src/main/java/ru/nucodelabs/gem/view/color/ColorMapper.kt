package ru.nucodelabs.gem.view.color

import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.IntegerProperty
import javafx.scene.paint.Color

interface ColorMapper {
    fun colorFor(value: Double): Color
    var minValue: Double
    var maxValue: Double
    var numberOfSegments: Int
    fun minValueProperty(): DoubleProperty
    fun maxValueProperty(): DoubleProperty
    fun numberOfSegmentsProperty(): IntegerProperty
    val segments: List<Segment>

    fun logScaleProperty(): BooleanProperty
    var isLogScale: Boolean

    data class Segment(val from: Double, val to: Double, val color: Color)
}