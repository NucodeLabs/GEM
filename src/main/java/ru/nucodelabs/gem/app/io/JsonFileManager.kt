package ru.nucodelabs.gem.app.io

import java.io.File

interface JsonFileManager {
    @Throws(Exception::class)
    fun <T> loadFromJson(jsonFile: File, type: Class<T>): T

    @Throws(Exception::class)
    fun <T> saveToJson(jsonFile: File, obj: T)
}