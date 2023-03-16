package ru.nucodelabs.gem.app.io.next

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.nucodelabs.gem.config.SaveState
import ru.nucodelabs.gem.config.State
import ru.nucodelabs.geo.anisotropy.Point
import java.io.File
import javax.inject.Inject

const val POINT_STATE = "POINT_STATE"
const val STATE_FILE = "STATE_FILE"

class StorageManager @Inject constructor(
    private val objectMapper: ObjectMapper
) {
    @SaveState
    @State(POINT_STATE)
    fun loadPoint(@State(STATE_FILE) file: File): Point {
        return objectMapper.readValue(file)
    }

    @SaveState
    fun savePoint(file: File, point: Point) {
        objectMapper.writeValue(file, point)
    }
}