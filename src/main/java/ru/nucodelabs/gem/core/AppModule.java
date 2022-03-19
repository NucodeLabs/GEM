package ru.nucodelabs.gem.core;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.FileChooser;
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

    @Provides
    @Named("EXP")
    private FileChooser provideEXPFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EXP - Полевые данные", "*.EXP", "*.exp")
        );
        return chooser;
    }

    @Provides
    @Named("JSON")
    private FileChooser provideJSONFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        return chooser;
    }

    @Provides
    @Named("MOD")
    private FileChooser provideMODFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MOD - Данные модели", "*.MOD", "*.mod")
        );
        return chooser;
    }

    @Provides
    @Named("Save")
    private Dialog<ButtonType> provideSaveDialog(@Named("CSS") String stylesheet) {
        Dialog<ButtonType> saveDialog = new Dialog<>();
        saveDialog.setTitle("Сохранение");
        saveDialog.setContentText("Сохранить изменения?");
        saveDialog.getDialogPane().getButtonTypes()
                .addAll(ButtonType.YES,
                        ButtonType.NO,
                        ButtonType.CANCEL);
        saveDialog.getDialogPane().getStylesheets().add(stylesheet);
        return saveDialog;
    }
}
