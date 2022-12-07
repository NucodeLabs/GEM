package ru.nucodelabs.gem.app.io

import ru.nucodelabs.geo.ves.ModelLayer
import ru.nucodelabs.geo.ves.Picket
import java.io.File

interface SonetImportManager {
    /**
     * Загружает имя и экспериментальные данные в пикет
     */
    @Throws(Exception::class)
    fun fromEXPFile(expFile: File): Picket

    /**
     * Загружает данные модели
     */
    @Throws(Exception::class)
    fun fromMODFile(modFile: File): List<ModelLayer>

    companion object Factory {
        @JvmStatic
        fun create(): SonetImportManager = SonetImportManagerImpl()
    }
}