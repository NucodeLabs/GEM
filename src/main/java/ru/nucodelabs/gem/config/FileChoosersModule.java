package ru.nucodelabs.gem.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static ru.nucodelabs.gem.app.pref.AppPreferences.*;

public class FileChoosersModule extends AbstractModule {

    private static final FileChooser.ExtensionFilter allFilesFilter = new FileChooser.ExtensionFilter("Все файлы", "*.*");

    @Provides
    @Singleton
    @Named(Name.File.EXP)
    private FileChooser provideEXPFileChooser(ResourceBundle ui, Preferences preferences) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(ui.getString("expData"), "*.exp"),
                allFilesFilter
        );
        File initDir = new File(preferences.get(EXP_FILES_DIR.getKey(), EXP_FILES_DIR.getDef()));
        if (initDir.exists()) {
            chooser.setInitialDirectory(initDir);
        }
        return chooser;
    }

    @Provides
    @Singleton
    @Named(Name.File.JSON)
    private FileChooser provideJSONFileChooser(ResourceBundle ui, Preferences preferences) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(ui.getString("section") + "/" + ui.getString("picket"), "*.json"),
                new FileChooser.ExtensionFilter(ui.getString("picket"), "*.point.json"),
                new FileChooser.ExtensionFilter(ui.getString("section"), "*.section.json"),
                new FileChooser.ExtensionFilter(ui.getString("a.point"), "*.ap.json"),
                allFilesFilter
        );
        File initDir = new File(preferences.get(JSON_FILES_DIR.getKey(), JSON_FILES_DIR.getDef()));
        if (initDir.exists()) {
            chooser.setInitialDirectory(initDir);
        }
        return chooser;
    }

    @Provides
    @Singleton
    @Named(Name.File.MOD)
    private FileChooser provideMODFileChooser(ResourceBundle ui, Preferences preferences) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(ui.getString("modData"), "*.mod"),
                allFilesFilter
        );
        File initDir = new File(preferences.get(MOD_FILES_DIR.getKey(), MOD_FILES_DIR.getDef()));
        if (initDir.exists()) {
            chooser.setInitialDirectory(initDir);
        }
        return chooser;
    }

    @Provides
    @Singleton
    @Named(Name.File.PNG)
    private FileChooser pngFileChooser(Preferences preferences) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Изображение", "*.png", "*.PNG"),
                allFilesFilter
        );
        File initDir = new File(preferences.get(PNG_FILES_DIR.getKey(), PNG_FILES_DIR.getDef()));
        if (initDir.exists()) {
            chooser.setInitialDirectory(initDir);
        }
        return chooser;
    }
}
