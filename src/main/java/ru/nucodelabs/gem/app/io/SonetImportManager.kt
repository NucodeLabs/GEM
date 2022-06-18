package ru.nucodelabs.gem.app.io

import ru.nucodelabs.data.ves.Picket
import java.io.File

interface SonetImportManager {
    /**
     * Загружает имя и экспериментальные данные в пикет
     */
    @Throws(Exception::class)
    fun loadNameAndExperimentalDataFromEXPFile(expFile: File, target: Picket): Picket

    /**
     * Загружает только экспериментальные данные в пикет
     */
    @Throws(Exception::class)
    fun loadExperimentalDataFromEXPFile(expFile: File, target: Picket): Picket

    /**
     * Загружает модельные данные в пикет
     */
    @Throws(Exception::class)
    fun loadModelDataFromMODFile(modFile: File, target: Picket): Picket

    companion object Factory {
        @JvmStatic
        fun create(): SonetImportManager = SonetImportManagerImpl()
    }
}