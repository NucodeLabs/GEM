package ru.nucodelabs.gem.app.io

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import java.io.File

internal class JacksonJsonFileManager @Inject constructor(
    private val objectMapper: ObjectMapper
) : JsonFileManager {
    @Throws(Exception::class)
    override fun <T> loadFromJson(jsonFile: File, type: Class<T>): T =
        objectMapper.readValue(jsonFile, type)

    @Throws(Exception::class)
    override fun <T> saveToJson(jsonFile: File, obj: T) = objectMapper.writeValue(jsonFile, obj)
}