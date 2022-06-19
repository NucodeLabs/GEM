package ru.nucodelabs.gem.view.main

import java.io.File

interface FileImporter {
    fun importEXP()
    fun addEXP(file: File)
    fun importMOD()
    fun importMOD(file: File)
    fun importJsonPicket()
    fun importJsonPicket(file: File)
}