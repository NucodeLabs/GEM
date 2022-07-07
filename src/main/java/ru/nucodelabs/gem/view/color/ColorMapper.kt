package ru.nucodelabs.gem.view.color

import javafx.beans.property.DoubleProperty
import javafx.beans.property.IntegerProperty
import javafx.scene.paint.Color
import ru.nucodelabs.gem.view.color_palette.ColorPalette

interface ColorMapper {
    fun colorFor(value: Double): Color
    var minValue: Double
    var maxValue: Double
    var blocksCnt: Int
    fun minValueProperty(): DoubleProperty
    fun maxValueProperty(): DoubleProperty
    fun blocksCntProperty(): IntegerProperty
    fun getColorBlockList(): List<ColorPalette.ColorBlock>
}