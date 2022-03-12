package ru.nucodelabs.gem.core;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.nucodelabs.gem.view.charts.MisfitStacksController;
import ru.nucodelabs.gem.view.charts.VESCurvesController;
import ru.nucodelabs.gem.view.main.MainViewController;
import ru.nucodelabs.gem.view.main.NoFileScreenController;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Creates and opens Views and giving them references to their Controllers linked with Models.
 * Also changing Views dynamically if ViewModel asks to do so.
 */
public class ViewService {

    private final ModelProvider modelProvider;
    private final ResourceBundle uiProperties;

    public ViewService(ModelProvider modelProvider, ResourceBundle uiProperties) {
        this.modelProvider = modelProvider;
        this.uiProperties = uiProperties;
    }

    public void start() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainViewController.class.getResource("MainSplitLayoutView.fxml"), uiProperties);
        Objects.requireNonNull(fxmlLoader);
        fxmlLoader.setControllerFactory(this::controllerFactory);
        try {
            ((Stage) fxmlLoader.load()).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Controller factory method
     *
     * @param type class of controller
     * @return new controller instance
     */
    private Object controllerFactory(Class<?> type) {
        if (type == MainViewController.class) {
            return new MainViewController(this, modelProvider.getSection());
        }
        if (type == NoFileScreenController.class) {
            return new NoFileScreenController();
        }
        if (type == MisfitStacksController.class) {
            return new MisfitStacksController(this);
        }
        if (type == VESCurvesController.class) {
            return new VESCurvesController(this);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Open native multiple *.EXP/*.exp files chooser owned by window associated with controller
     *
     * @param stage owner stage
     * @return chosen files to open, or NULL if no file selected
     */
    public List<File> showOpenEXPFileChooser(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл полевых данных для интерпретации");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EXP - Полевые данные", "*.EXP", "*.exp")
        );
        return chooser.showOpenMultipleDialog(stage);
    }

    /**
     * Open native *.MOD/*.mod file chooser owned by window associated with controller
     *
     * @param stage owner stage
     * @return chosen file to open, NULL if no file selected
     */
    public File showOpenMODFileChooser(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл модели"); //TODO: translation
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MOD - Данные модели", "*.MOD", "*.mod")
        );
        return chooser.showOpenDialog(stage);
    }

    /**
     * Open native *.json file chooser owned by window associated with controller
     *
     * @param stage owner stage
     * @return chosen file to open, NULL if no file selected
     */
    public File showSaveJsonFileChooser(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(uiProperties.getString("save") + " " + uiProperties.getString("section"));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        return chooser.showSaveDialog(stage);
    }

    /**
     * Open native *.json file chooser owned by window associated with controller
     *
     * @param stage owner stage
     * @return chosen file to save, NULL if no file selected
     */
    public File showOpenJsonFileChooser(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(uiProperties.getString("open") + " " + uiProperties.getString("section"));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        return chooser.showOpenDialog(stage);
    }

    public void alertExperimentalDataIsUnsafe(Stage stage, String picketName) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(uiProperties.getString("compatibilityMode"));
        alert.setHeaderText(picketName + " - " + uiProperties.getString("EXPSTTMismatch"));
        alert.setContentText(uiProperties.getString("minimalDataWillBeDisplayed"));
        alert.initOwner(stage);
        alert.getDialogPane().getStylesheets().add("ru/nucodelabs/gem/view/common.css");
        alert.show();
    }

    public void alertNoLib(Stage stage, UnsatisfiedLinkError e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(uiProperties.getString("unableToDrawChart"));
        alert.setHeaderText(uiProperties.getString("noLib"));
        alert.setContentText(e.getMessage());
        alert.initOwner(stage);
        alert.getDialogPane().getStylesheets().add("ru/nucodelabs/gem/view/common.css");
        alert.show();
    }

    public void alertIncorrectFile(Stage stage, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle("Неверный формат файла");
        alert.setHeaderText(uiProperties.getString("fileError"));
        alert.setContentText(e.getMessage());
        alert.initOwner(stage);
        alert.getDialogPane().getStylesheets().add("ru/nucodelabs/gem/view/common.css");
        alert.show();
    }
}
