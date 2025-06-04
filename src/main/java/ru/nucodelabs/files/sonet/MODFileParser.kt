package ru.nucodelabs.files.sonet

import java.io.File

class MODFileParser(private val modFile: File) {
    @Throws(Exception::class)
    fun parse(): MODFile = SonetImportUtils.readMOD(modFile)
}
