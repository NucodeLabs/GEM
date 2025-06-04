package ru.nucodelabs.files.sonet

import java.io.File

class STTFile {
    var file: File? = null
    var AB_2: MutableList<Double> = mutableListOf()
    var MN_2: MutableList<Double> = mutableListOf()
    val columnCnt: Int
        get() = 2
}
