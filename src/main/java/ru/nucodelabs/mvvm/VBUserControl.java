package ru.nucodelabs.mvvm;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class VBUserControl extends VBox {
    public VBUserControl() {
        ResourceBundle bundle = ResourceBundle.getBundle("ru/nucodelabs/gem/UI", new Locale("ru"));
        String fileName = this.getClass().getSimpleName() + ".fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName), bundle);
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
