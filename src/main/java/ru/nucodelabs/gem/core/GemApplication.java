package ru.nucodelabs.gem.core;

import javafx.stage.Stage;

import java.util.ResourceBundle;

/**
 * Starting up everything.
 */
public class GemApplication extends javafx.application.Application {
    @Override
    public void start(Stage stage) {
        ModelFactory modelFactory = new ModelFactory();
        ResourceBundle uiProperties = ResourceBundle.getBundle("ru/nucodelabs/gem/UI");
        ViewManager viewManager = new ViewManager(modelFactory, uiProperties);

        viewManager.start();
    }
}
