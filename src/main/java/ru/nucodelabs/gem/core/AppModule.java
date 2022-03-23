package ru.nucodelabs.gem.core;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import ru.nucodelabs.gem.view.main.MainViewController;
import ru.nucodelabs.gem.view.main.MainViewModule;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Зависимости приложения, которое, по сути, создает MainView
 */
public class AppModule extends AbstractModule {
    @Provides
    @Singleton
    private ResourceBundle provideUIProperties() {
        return ResourceBundle.getBundle("ru/nucodelabs/gem/UI");
    }

    @Provides
    @Named("CSS")
    private String provideStylesheet() {
        return "ru/nucodelabs/gem/view/common.css";
    }

    @Provides
    @Named("MainView")
    private FXMLLoader provideFXMLLoader(ResourceBundle uiProperties) {
        return new FXMLLoader(MainViewController.class.getResource("MainSplitLayoutView.fxml"), uiProperties);
    }

    @Provides
    @Named("MainView")
    public Stage create(Injector injector, @Named("MainView") FXMLLoader loader) throws IOException {
        loader.setControllerFactory(injector.createChildInjector(new MainViewModule())::getInstance);
        return loader.load();
    }
}
