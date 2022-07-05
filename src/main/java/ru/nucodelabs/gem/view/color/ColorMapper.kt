package ru.nucodelabs.gem.view.color

import javafx.beans.property.DoubleProperty
import javafx.scene.paint.Color

interface ColorMapper {
    fun colorFor(value: Double): Color
    var minValue: Double
    var maxValue: Double
    fun minValueProperty(): DoubleProperty
    fun maxValueProperty(): DoubleProperty
}