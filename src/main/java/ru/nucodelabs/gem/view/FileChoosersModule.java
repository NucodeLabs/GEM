package ru.nucodelabs.gem.view;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static ru.nucodelabs.gem.app.pref.AppPreferencesKt.*;

public class FileChoosersModule extends AbstractModule {

    private static final FileChooser.ExtensionFilter allFilesFilter = new FileChooser.ExtensionFilter("All files", "*.*");

    @Provides
    @Singleton
    @Named("EXP")
    private FileChooser provideEXPFileChooser(ResourceBundle ui, Preferences preferences) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(ui.getString("expData"), "*.EXP", "*.exp"),
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
    @Named("JSON")
    private FileChooser provideJSONFileChooser(ResourceBundle ui, Preferences preferences) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(ui.getString("section") + "/" + ui.getString("picket"), "*.json", "*.JSON"),
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
    @Named("MOD")
    private FileChooser provideMODFileChooser(ResourceBundle ui, Preferences preferences) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(ui.getString("modData"), "*.MOD", "*.mod"),
                allFilesFilter
        );
        File initDir = new File(preferences.get(MOD_FILES_DIR.getKey(), MOD_FILES_DIR.getDef()));
        if (initDir.exists()) {
            chooser.setInitialDirectory(initDir);
        }
        return chooser;
    }
}
