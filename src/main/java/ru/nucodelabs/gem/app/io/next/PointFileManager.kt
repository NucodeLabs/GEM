package ru.nucodelabs.gem.app.io.next

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.nucodelabs.geo.anisotropy.Point
import java.io.File
import javax.inject.Inject


class PointFileManager @Inject constructor(
    private val objectMapper: ObjectMapper
) {
    fun loadPoint(json: String): Point {
        return objectMapper.readValue(json)
    }

    fun savePoint(file: File, point: Point) {
        objectMapper.writeValue(file, point)
    }
}