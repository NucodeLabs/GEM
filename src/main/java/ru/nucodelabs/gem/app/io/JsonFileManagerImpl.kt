package ru.nucodelabs.gem.app.io

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import java.io.File
import java.io.Serializable

internal class JsonFileManagerImpl : JsonFileManager {
    private val objectMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

    @Throws(Exception::class)
    override fun <T : Serializable> loadFromJson(jsonFile: File, type: Class<T>): T =
        objectMapper.readValue(jsonFile, type)

    @Throws(Exception::class)
    override fun <T : Serializable> saveToJson(jsonFile: File, obj: T) = objectMapper.writeValue(jsonFile, obj)
}