package ru.nucodelabs.files.sonet

import java.io.File

class STTFileParser(private val sttFile: File) {
    @Throws(Exception::class)
    fun parse(): STTFile = SonetImportUtils.readSTT(sttFile)
}
