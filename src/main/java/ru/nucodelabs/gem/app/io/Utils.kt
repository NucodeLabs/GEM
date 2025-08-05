package ru.nucodelabs.gem.app.io

import javafx.stage.FileChooser
import org.slf4j.LoggerFactory
import ru.nucodelabs.gem.app.pref.Preference
import java.io.File
import java.util.prefs.Preferences

fun saveInitialDirectory(
    preferences: Preferences,
    initDirPref: Preference<String>,
    fileChooser: FileChooser,
    file: File?,
) {
    if (file != null && file.parentFile.isDirectory) {
        fileChooser.initialDirectory = file.parentFile
        preferences.put(initDirPref.key, file.parentFile.absolutePath)
    }
}

fun slf4j(instance: Any) = LoggerFactory.getLogger(instance::class.java)!!