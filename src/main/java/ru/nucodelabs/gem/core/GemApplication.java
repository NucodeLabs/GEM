package ru.nucodelabs.gem.core;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.nucodelabs.gem.view.MainViewFactory;

import java.io.IOException;

/**
 * Starting up everything.
 */
public class GemApplication extends Application {

    @Override
    public void start(Stage stage) {
        try {
            new MainViewFactory().create().show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
