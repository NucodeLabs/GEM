package ru.nucodelabs.files.sonet

import java.io.File

class EXPFile {
    var STTFileName: String = ""
    var file: File? = null

    var number: String = ""
    var date: String = ""
    var weather: String = ""
    var operator: String = ""
    var interpreter: String = ""
    var checked: String = ""

    var amperage: MutableList<Double> = mutableListOf()
    var voltage: MutableList<Double> = mutableListOf()
    var resistanceApparent: MutableList<Double> = mutableListOf()
    var errorResistanceApparent: MutableList<Double> = mutableListOf()
    var polarizationApparent: MutableList<Double> = mutableListOf()
    var errorPolarizationApparent: MutableList<Double> = mutableListOf()

    val columnCnt: Int
        get() = 6
}
