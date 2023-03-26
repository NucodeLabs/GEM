package ru.nucodelabs.gem.app.io

import javafx.stage.FileChooser
import ru.nucodelabs.gem.app.pref.Preference
import ru.nucodelabs.kfx.core.AbstractViewController
import java.io.File
import java.util.prefs.Preferences

fun AbstractViewController<*>.saveInitialDirectory(
    preferences: Preferences,
    initDirPref: Preference<String>,
    fileChooser: FileChooser,
    file: File?,
) {
    if (file != null) {
        if (file.parentFile.isDirectory) {
            fileChooser.initialDirectory = file.parentFile
            preferences.put(initDirPref.key, file.parentFile.absolutePath)
        }
    }
}