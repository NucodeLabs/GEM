package ru.nucodelabs.gem.app;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import ru.nucodelabs.gem.app.io.JsonFileManager;
import ru.nucodelabs.gem.app.io.SonetImportManager;
import ru.nucodelabs.gem.view.FileChoosersModule;
import ru.nucodelabs.gem.view.main.MainViewController;
import ru.nucodelabs.gem.view.main.MainViewModule;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * Зависимости приложения, которое, по сути, создает MainView
 */
public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FileChoosersModule());
    }

    @Provides
    @Singleton
    private ResourceBundle provideUIProperties() {
        return ResourceBundle.getBundle("ru/nucodelabs/gem/UI");
    }

    @Provides
    @Named("CSS")
    @Singleton
    private String provideStylesheet() {
        return "ru/nucodelabs/gem/view/common.css";
    }

    @Provides
    @Named("MainView")
    private URL provideMainViewFXML() {
        return MainViewController.class.getResource("MainSplitLayoutView.fxml");
    }

    @Provides
    @Named("MainView")
    private FXMLLoader provideFXMLLoader(ResourceBundle uiProperties, Injector injector, @Named("MainView") URL url) {
        FXMLLoader fxmlLoader = new FXMLLoader(url, uiProperties);
        fxmlLoader.setControllerFactory(injector.createChildInjector(new MainViewModule())::getInstance);
        return fxmlLoader;
    }

    @Provides
    @Named("MainView")
    private Stage create(@Named("MainView") FXMLLoader loader) throws IOException {
        return loader.load();
    }

    @Provides
    private Validator provideValidator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Provides
    @Singleton
    private JsonFileManager provideJsonFileManager() {
        return JsonFileManager.createDefault();
    }

    @Provides
    @Singleton
    private SonetImportManager provideSonetImportManager() {
        return SonetImportManager.create();
    }

    @Provides
    private Preferences preferences() {
        return Preferences.userNodeForPackage(GemApplication.class);
    }
}
