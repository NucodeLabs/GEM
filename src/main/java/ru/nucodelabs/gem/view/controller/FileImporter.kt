package ru.nucodelabs.gem.view.controller

import java.io.File

interface FileImporter {
    fun importEXP()
    fun importEXP(file: File)
    fun importMOD()
    fun importMOD(file: File)
    fun importJsonPicket()
    fun importJsonPicket(file: File)
}