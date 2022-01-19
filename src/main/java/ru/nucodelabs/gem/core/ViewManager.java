package ru.nucodelabs.gem.core;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.nucodelabs.gem.view.main.MainSplitLayoutView;
import ru.nucodelabs.gem.view.main.MainViewModel;
import ru.nucodelabs.gem.view.welcome.WelcomeView;
import ru.nucodelabs.gem.view.welcome.WelcomeViewModel;

import java.io.File;

/**
 * <h2>View Manager</h2>
 * Creates and opens Views and giving them references to their ViewModels linked with Models.
 * Also changing Views dynamically if ViewModel asks to do so.
 */
public class ViewManager {

    private final ModelFactory modelFactory;
    private final Stage stage;

    public ViewManager(ModelFactory modelFactory, Stage stage) {
        this.modelFactory = modelFactory;
        this.stage = stage;
    }

    public void start() {
        WelcomeView welcomeView = new WelcomeView(
                new WelcomeViewModel(this)
        );

        Scene scene = new Scene(welcomeView);
        stage.setTitle("GEM");
        stage.getIcons().add(new Image("img/gem.png"));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    public void openMainViewWithImportEXP() {
        File expFile = showEXPFileChooser();
        if (expFile != null) {
            MainSplitLayoutView mainSplitLayoutView = new MainSplitLayoutView(
                    new MainViewModel(modelFactory.getVesDataModel(), this)
            );
            Scene scene = new Scene(mainSplitLayoutView);
            stage.hide();
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();
            stage.setMaximized(true);
            mainSplitLayoutView.getViewModel().importEXP(expFile);
        }
    }

    public File showEXPFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл полевых данных для интерпретации");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EXP - Полевые данные", "*.EXP", "*.exp")
        );
        return chooser.showOpenDialog(stage);
//      если закрыть окно выбора файла, ничего не выбрав, то FileChooser вернет null
    }

    public File showMODFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл модели");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MOD - Данные модели", "*.MOD", "*.mod")
        );
        return chooser.showOpenDialog(stage);
    }

    public Stage getStage() {
        return stage;
    }
}
