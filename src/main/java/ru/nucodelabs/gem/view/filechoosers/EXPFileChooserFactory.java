package ru.nucodelabs.gem.view.filechoosers;

import javafx.stage.FileChooser;

public class EXPFileChooserFactory implements FileChooserFactory {
    @Override
    public FileChooser create() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EXP - Полевые данные", "*.EXP", "*.exp")
        );
        return chooser;
    }
}
