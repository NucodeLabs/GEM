package ru.nucodelabs.gem.core;

import javafx.stage.Stage;

import java.util.ResourceBundle;

/**
 * Starting up everything.
 */
public class GemApplication extends javafx.application.Application {
    @Override
    public void start(Stage stage) {
        ModelProvider modelProvider = new ModelProvider();
        ResourceBundle uiProperties = ResourceBundle.getBundle("ru/nucodelabs/gem/UI");
        ViewService viewService = new ViewService(modelProvider, uiProperties);

        viewService.start();
    }
}
