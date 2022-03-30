package ru.nucodelabs.gem.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Приложение, создает главное окошко
 */
public class GemApplication extends Application {
    @Override
    public void start(Stage stage) {
        Injector injector = Guice.createInjector(new AppModule());
        injector.getInstance(Key.get(Stage.class, Names.named("MainView"))).show();
    }
}
