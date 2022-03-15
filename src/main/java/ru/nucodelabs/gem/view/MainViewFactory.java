package ru.nucodelabs.gem.view;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import ru.nucodelabs.gem.view.main.MainViewController;

import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainViewFactory {
    public Stage create() throws IOException {
        ResourceBundle uiProperties = ResourceBundle.getBundle("ru/nucodelabs/gem/UI");
        FXMLLoader fxmlLoader = new FXMLLoader(MainViewController.class.getResource("MainSplitLayoutView.fxml"), uiProperties);
        Objects.requireNonNull(fxmlLoader);
        Injector injector = Guice.createInjector(new MainViewModule());
        fxmlLoader.setControllerFactory(injector::getInstance);
        return fxmlLoader.load();
    }
}
