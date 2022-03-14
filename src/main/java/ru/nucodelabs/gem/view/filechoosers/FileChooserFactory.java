package ru.nucodelabs.gem.view.filechoosers;

import javafx.stage.FileChooser;

@FunctionalInterface
public interface FileChooserFactory {
    FileChooser create();
}
