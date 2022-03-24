package ru.nucodelabs.gem.view;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import javafx.stage.FileChooser;

public class FileChoosersModule extends AbstractModule {
    @Provides
    @Singleton
    @Named("EXP")
    private FileChooser provideEXPFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EXP - Полевые данные", "*.EXP", "*.exp")
        );
        return chooser;
    }

    @Provides
    @Singleton
    @Named("JSON")
    private FileChooser provideJSONFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        return chooser;
    }

    @Provides
    @Singleton
    @Named("MOD")
    private FileChooser provideMODFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MOD - Данные модели", "*.MOD", "*.mod")
        );
        return chooser;
    }
}