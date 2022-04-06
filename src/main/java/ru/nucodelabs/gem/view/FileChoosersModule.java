package ru.nucodelabs.gem.view;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import javafx.stage.FileChooser;

import java.util.ResourceBundle;

public class FileChoosersModule extends AbstractModule {

    private static final FileChooser.ExtensionFilter allFilesFilter = new FileChooser.ExtensionFilter("All files", "*.*");

    @Provides
    @Singleton
    @Named("EXP")
    private FileChooser provideEXPFileChooser(ResourceBundle ui) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EXP - " + ui.getString("expData"), "*.EXP", "*.exp"),
                allFilesFilter
        );
        return chooser;
    }

    @Provides
    @Singleton
    @Named("JSON")
    private FileChooser provideJSONFileChooser(ResourceBundle ui) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON - " + ui.getString("section"), "*.json, *.JSON"),
                allFilesFilter
        );
        return chooser;
    }

    @Provides
    @Singleton
    @Named("MOD")
    private FileChooser provideMODFileChooser(ResourceBundle ui) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MOD - " + ui.getString("modData"), "*.MOD", "*.mod"),
                allFilesFilter
        );
        return chooser;
    }
}
