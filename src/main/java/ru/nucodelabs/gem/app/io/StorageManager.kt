package ru.nucodelabs.gem.app.io

import ru.nucodelabs.data.ves.Section
import ru.nucodelabs.gem.app.snapshot.Snapshot
import java.io.File
import java.io.Serializable
import javax.inject.Inject

class StorageManager @Inject constructor(
    private val jsonFileManager: JsonFileManager,
    private val sonetImportManager: SonetImportManager,
) : JsonFileManager by jsonFileManager, SonetImportManager by sonetImportManager {
    var savedSnapshot: Snapshot<Section> = Snapshot.of(Section.DEFAULT)
        private set
    var savedSnapshotFile: File? = null
        private set

    fun resetSavedSnapshot() {
        savedSnapshot = Snapshot.of(Section.DEFAULT)
        savedSnapshotFile = null
    }

    override fun <T : Serializable> loadFromJson(jsonFile: File, type: Class<T>): T {
        val loaded = jsonFileManager.loadFromJson(jsonFile, type)
        if (loaded is Section) {
            savedSnapshot = Snapshot.of(loaded)
            savedSnapshotFile = jsonFile
        }
        return loaded
    }

    override fun <T : Serializable> saveToJson(jsonFile: File, obj: T) {
        jsonFileManager.saveToJson(jsonFile, obj)
        if (obj is Section) {
            savedSnapshot = Snapshot.of(obj)
            savedSnapshotFile = jsonFile
        }
    }
}