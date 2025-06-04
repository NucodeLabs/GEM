package ru.nucodelabs.files.sonet

import java.io.File

class EXPFileParser(private val expFile: File) {
    @Throws(Exception::class)
    fun parse(): EXPFile = SonetImportUtils.readEXP(expFile)
}
