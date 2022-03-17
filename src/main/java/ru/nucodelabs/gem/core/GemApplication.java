package ru.nucodelabs.gem.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;
import ru.nucodelabs.gem.view.main.MainViewFactory;

import java.io.IOException;

/**
 * Приложение, создает главное окошко
 */
public class GemApplication extends Application {
    @Override
    public void start(Stage stage) {
        Injector injector = Guice.createInjector(new AppModule());
        try {
            injector.getInstance(MainViewFactory.class).create().show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
