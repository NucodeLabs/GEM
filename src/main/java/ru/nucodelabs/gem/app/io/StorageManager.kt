package ru.nucodelabs.gem.app.io

import jakarta.inject.Inject
import ru.nucodelabs.geo.ves.Section
import ru.nucodelabs.kfx.snapshot.Snapshot
import java.io.File

class StorageManager @Inject constructor(
    private val jsonFileManager: JsonFileManager,
    private val sonetImportManager: SonetImportManager,
) : JsonFileManager by jsonFileManager, SonetImportManager by sonetImportManager {
    var savedSnapshot: Snapshot<Section> = Snapshot.of(Section())
        private set
    var savedSnapshotFile: File? = null
        private set

    fun resetSavedSnapshot() {
        savedSnapshot = Snapshot.of(Section())
        savedSnapshotFile = null
    }

    override fun <T> loadFromJson(jsonFile: File, type: Class<T>): T {
        val loaded = jsonFileManager.loadFromJson(jsonFile, type)
        if (loaded is Section) {
            savedSnapshot = Snapshot.of(loaded)
            savedSnapshotFile = jsonFile
        }
        return loaded
    }

    override fun <T> saveToJson(jsonFile: File, obj: T) {
        jsonFileManager.saveToJson(jsonFile, obj)
        if (obj is Section) {
            savedSnapshot = Snapshot.of(obj)
            savedSnapshotFile = jsonFile
        }
    }
}