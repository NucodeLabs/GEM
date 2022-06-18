package ru.nucodelabs.gem.app.io

import java.io.File
import java.io.Serializable

interface JsonFileManager {
    @Throws(Exception::class)
    fun <T : Serializable> loadFromJson(jsonFile: File, type: Class<T>): T

    @Throws(Exception::class)
    fun <T : Serializable> saveToJson(jsonFile: File, obj: T)

    companion object Factory {
        @JvmStatic
        fun createDefault(): JsonFileManager = JsonFileManagerImpl()
    }
}