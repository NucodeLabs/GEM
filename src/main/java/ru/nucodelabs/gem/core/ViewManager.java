package ru.nucodelabs.gem.core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.main.MainViewController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Creates and opens Views and giving them references to their ViewModels linked with Models.
 * Also changing Views dynamically if ViewModel asks to do so.
 */
public class ViewManager {

    private final ControllerFactory controllerFactory;
    private final ResourceBundle uiProperties;
    private final Map<Controller, Stage> controllerStageMap;

    public ViewManager(ModelFactory modelFactory, ResourceBundle uiProperties) {
        this.controllerFactory = new ControllerFactory(this, modelFactory);
        this.uiProperties = uiProperties;
        controllerStageMap = new HashMap<>();
    }

    /**
     * Opens new main view
     */
    public void start() {
        FXMLLoader fxmlLoader = null;
        fxmlLoader = new FXMLLoader(MainViewController.class.getResource("MainSplitLayoutView.fxml"), uiProperties);
        Objects.requireNonNull(fxmlLoader);
        fxmlLoader.setControllerFactory(controllerFactory::create);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(new Scene(fxmlLoader.getRoot()));
        stage.setWidth(1280);
        stage.setHeight(720);
        stage.show();
        controllerStageMap.put(fxmlLoader.getController(), stage);
    }

    /**
     * Closes stage (window) that associated with controller
     *
     * @param caller controller that calls
     */
    public void close(Controller caller) {
        controllerStageMap.get(caller).close();
    }

    /**
     * Open native multiple *.EXP/*.exp files chooser owned by window associated with controller
     *
     * @param caller controller that calls
     * @return chosen files to open, or NULL if no file selected
     */
    public List<File> showOpenEXPFileChooser(Controller caller) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл полевых данных для интерпретации");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EXP - Полевые данные", "*.EXP", "*.exp")
        );
        return chooser.showOpenMultipleDialog(controllerStageMap.get(caller));
    }

    /**
     * Open native *.MOD/*.mod file chooser owned by window associated with controller
     *
     * @param caller controller that calls
     * @return chosen file to open, NULL if no file selected
     */
    public File showOpenMODFileChooser(Controller caller) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл модели"); //TODO: translation
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MOD - Данные модели", "*.MOD", "*.mod")
        );
        return chooser.showOpenDialog(controllerStageMap.get(caller));
    }

    /**
     * Open native *.json file chooser owned by window associated with controller
     *
     * @param caller controller that calls
     * @return chosen file to open, NULL if no file selected
     */
    public File showSaveJsonFileChooser(Controller caller) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(uiProperties.getString("save") + " " + uiProperties.getString("section"));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        return chooser.showSaveDialog(controllerStageMap.get(caller));
    }

    /**
     * Open native *.json file chooser owned by window associated with controller
     *
     * @param caller controller that calls
     * @return chosen file to save, NULL if no file selected
     */
    public File showOpenJsonFileChooser(Controller caller) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(uiProperties.getString("open") + " " + uiProperties.getString("section"));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        return chooser.showOpenDialog(controllerStageMap.get(caller));
    }

    public void alertExperimentalDataIsUnsafe(Controller caller, String picketName) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(uiProperties.getString("compatibilityMode"));
        alert.setHeaderText(picketName + " - " + uiProperties.getString("EXPSTTMismatch"));
        alert.setContentText(uiProperties.getString("minimalDataWillBeDisplayed"));
        alert.initOwner(controllerStageMap.get(caller));
        alert.getDialogPane().getStylesheets().add("ru/nucodelabs/gem/view/common.css");
        alert.show();
    }

    public void alertFileNotFound(Controller caller, FileNotFoundException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(uiProperties.getString("error"));
        alert.setHeaderText(uiProperties.getString("fileNotFound"));
        alert.setContentText(e.getMessage());
        alert.initOwner(controllerStageMap.get(caller));
        alert.getDialogPane().getStylesheets().add("ru/nucodelabs/gem/view/common.css");
        alert.show();
    }

    public void alertNoLib(Controller caller, UnsatisfiedLinkError e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(uiProperties.getString("unableToDrawChart"));
        alert.setHeaderText(uiProperties.getString("noLib"));
        alert.setContentText(e.getMessage());
        alert.initOwner(controllerStageMap.get(caller));
        alert.getDialogPane().getStylesheets().add("ru/nucodelabs/gem/view/common.css");
        alert.show();
    }

    public void alertIncorrectFile(Controller caller, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle("Неверный формат файла");
        alert.setHeaderText(uiProperties.getString("fileError"));
        alert.setContentText(e.getMessage());
        alert.initOwner(controllerStageMap.get(caller));
        alert.getDialogPane().getStylesheets().add("ru/nucodelabs/gem/view/common.css");
        alert.show();
    }
}
