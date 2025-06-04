package ru.nucodelabs.files.sonet

import java.io.File

class MODFile {
    var file: File? = null

    var resistance: MutableList<Double> = mutableListOf()
    var polarization: MutableList<Double> = mutableListOf()
    var power: MutableList<Double> = mutableListOf()

    val columnCnt: Int
        get() = 3
}
