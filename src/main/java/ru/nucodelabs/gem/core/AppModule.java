package ru.nucodelabs.gem.core;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import javafx.fxml.FXMLLoader;
import ru.nucodelabs.gem.view.main.MainViewController;

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
    @Named("MainView")
    private FXMLLoader provideFXMLLoader(ResourceBundle uiProperties) {
        return new FXMLLoader(MainViewController.class.getResource("MainSplitLayoutView.fxml"), uiProperties);
    }
}
