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
import java.util.ResourceBundle;

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
    private FXMLLoader provideFXMLLoader(ResourceBundle uiProperties) {
        return new FXMLLoader(MainViewController.class.getResource("MainSplitLayoutView.fxml"), uiProperties);
    }

    @Provides
    @Named("MainView")
    private Stage create(Injector injector, @Named("MainView") FXMLLoader loader) throws IOException {
        loader.setControllerFactory(injector.createChildInjector(new MainViewModule())::getInstance);
        return loader.load();
    }

    @Provides
    private Validator provideValidator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Provides
    @Singleton
    private JsonFileManager provideJsonFileManager() {
        return JsonFileManager.createDefaultFileManager();
    }

    @Provides
    @Singleton
    private SonetImportManager provideSonetImportManager() {
        return SonetImportManager.create();
    }
}
