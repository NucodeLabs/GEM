package ru.nucodelabs.gem.view.color

import javafx.beans.property.DoubleProperty
import javafx.beans.property.IntegerProperty
import javafx.scene.paint.Color

interface ColorMapper {
    fun colorFor(value: Double): Color
    var minValue: Double
    var maxValue: Double
    var blocksCount: Int
    fun minValueProperty(): DoubleProperty
    fun maxValueProperty(): DoubleProperty
    fun blocksCountProperty(): IntegerProperty
    val colorBlocks: List<ColorBlock>

    data class ColorBlock(val from: Double, val to: Double, val color: Color)
}