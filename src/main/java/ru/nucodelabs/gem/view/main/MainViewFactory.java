package ru.nucodelabs.gem.view.main;

import com.google.inject.Injector;
import com.google.inject.name.Named;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Реализует factory method для создания нового MainView
 */
public class MainViewFactory {
    @Inject
    @Named("MainView")
    private FXMLLoader loader;

    @Inject
    private Injector injector;

    public Stage create() throws IOException {
        loader.setControllerFactory(
                injector.createChildInjector(new MainViewModule())::getInstance);
        return loader.load();
    }
}
