package ru.nucodelabs.gem.view

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.google.inject.name.Named
import javafx.stage.FileChooser
import java.io.File
import java.util.ResourceBundle
import java.util.prefs.Preferences
import ru.nucodelabs.gem.app.pref.EXP_FILES_DIR
import ru.nucodelabs.gem.app.pref.JSON_FILES_DIR
import ru.nucodelabs.gem.app.pref.MOD_FILES_DIR
import ru.nucodelabs.gem.app.pref.PNG_FILES_DIR

class FileChoosersModule : AbstractModule() {
    private val allFilesFilter = FileChooser.ExtensionFilter("Все файлы", "*.*")

    @Provides
    @Singleton
    @Named("EXP")
    fun provideEXPFileChooser(ui: ResourceBundle, preferences: Preferences): FileChooser =
        FileChooser().apply {
            extensionFilters.addAll(
                FileChooser.ExtensionFilter(ui.getString("expData"), "*.exp"),
                allFilesFilter
            )
            val initDir = File(preferences.get(EXP_FILES_DIR.key, EXP_FILES_DIR.def))
            if (initDir.exists()) initialDirectory = initDir
        }

    @Provides
    @Singleton
    @Named("JSON")
    fun provideJSONFileChooser(ui: ResourceBundle, preferences: Preferences): FileChooser =
        FileChooser().apply {
            extensionFilters.addAll(
                FileChooser.ExtensionFilter("${ui.getString("section")}/${ui.getString("picket")}", "*.json"),
                FileChooser.ExtensionFilter(ui.getString("picket"), "*.point.json"),
                FileChooser.ExtensionFilter(ui.getString("section"), "*.section.json"),
                allFilesFilter
            )
            val initDir = File(preferences.get(JSON_FILES_DIR.key, JSON_FILES_DIR.def))
            if (initDir.exists()) initialDirectory = initDir
        }

    @Provides
    @Singleton
    @Named("MOD")
    fun provideMODFileChooser(ui: ResourceBundle, preferences: Preferences): FileChooser =
        FileChooser().apply {
            extensionFilters.addAll(
                FileChooser.ExtensionFilter(ui.getString("modData"), "*.mod"),
                allFilesFilter
            )
            val initDir = File(preferences.get(MOD_FILES_DIR.key, MOD_FILES_DIR.def))
            if (initDir.exists()) initialDirectory = initDir
        }

    @Provides
    @Singleton
    @Named("PNG")
    fun pngFileChooser(preferences: Preferences): FileChooser =
        FileChooser().apply {
            extensionFilters.addAll(
                FileChooser.ExtensionFilter("Изображение", "*.png", "*.PNG"),
                allFilesFilter
            )
            val initDir = File(preferences.get(PNG_FILES_DIR.key, PNG_FILES_DIR.def))
            if (initDir.exists()) initialDirectory = initDir
        }
}
