package ru.nucodelabs.gem.view.filechoosers;

import javafx.stage.FileChooser;

public class JsonFileChooserFactory implements FileChooserFactory {
    @Override
    public FileChooser create() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        return chooser;
    }
}
