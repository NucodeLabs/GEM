package ru.nucodelabs.gem.view.filechoosers;

import javafx.stage.FileChooser;

public class MODFileChooserFactory implements FileChooserFactory {
    @Override
    public FileChooser create() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MOD - Данные модели", "*.MOD", "*.mod")
        );
        return chooser;
    }
}
