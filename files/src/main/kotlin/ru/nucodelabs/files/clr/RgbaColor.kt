package ru.nucodelabs.files.clr

data class RgbaColor(
    val r: Int,
    val g: Int,
    val b: Int,
    val a: Int,
) {
    val opacity: Double = a.toDouble() / 255.0
}
