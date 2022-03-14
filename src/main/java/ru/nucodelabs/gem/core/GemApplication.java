package ru.nucodelabs.gem.core;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import ru.nucodelabs.gem.core.events.NewWindowRequest;
import ru.nucodelabs.gem.view.main.MainViewController;

import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Starting up everything.
 */
public class GemApplication extends Application {
    private final ResourceBundle uiProperties = ResourceBundle.getBundle("ru/nucodelabs/gem/UI");
    private final EventBus eventBus = new EventBus();

    @Override
    public void start(Stage stage) {
        eventBus.register(this);

        showMainView();
    }

    private void showMainView() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainViewController.class.getResource("MainSplitLayoutView.fxml"), uiProperties);
        Objects.requireNonNull(fxmlLoader);
        fxmlLoader.setControllerFactory(new ControllerFactory(eventBus));
        try {
            ((Stage) fxmlLoader.load()).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    private void handleNewWindowRequest(NewWindowRequest request) {
        this.showMainView();
    }
}
