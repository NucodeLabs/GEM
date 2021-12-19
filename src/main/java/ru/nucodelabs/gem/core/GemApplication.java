package ru.nucodelabs.gem.core;

import javafx.stage.Stage;

/**
 * <h2>Application</h2>
 * Starting up everything.
 */
public class GemApplication extends javafx.application.Application {
    @Override
    public void start(Stage stage) {
        ModelFactory modelFactory = new ModelFactory();
        ViewManager viewManager = new ViewManager(modelFactory, stage);

        viewManager.start();
    }
}
