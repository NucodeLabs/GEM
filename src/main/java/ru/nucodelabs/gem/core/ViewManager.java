package ru.nucodelabs.gem.core;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.nucodelabs.gem.view.main.MainSplitLayoutView;
import ru.nucodelabs.gem.view.main.MainViewModel;
import ru.nucodelabs.mvvm.VBView;
import ru.nucodelabs.mvvm.ViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Creates and opens Views and giving them references to their ViewModels linked with Models.
 * Also changing Views dynamically if ViewModel asks to do so.
 */
public class ViewManager {

    private final ViewModelFactory viewModelFactory;
    private final Map<ViewModel, Stage> viewModelStageMap;
    private final ResourceBundle uiProperties;

    public ViewManager(ViewModelFactory viewModelFactory, ResourceBundle uiProperties) {
        this.uiProperties = uiProperties;
        this.viewModelFactory = viewModelFactory;
        viewModelFactory.initViewManager(this);
        viewModelStageMap = new HashMap<>();
    }

    /**
     * Opens welcome window
     */
    public void start() {
        MainViewModel mainViewModel = viewModelFactory.createMainViewModel();
        MainSplitLayoutView mainSplitLayoutView = new MainSplitLayoutView(mainViewModel);
        initAndShowWindow("GEM", mainSplitLayoutView, mainViewModel);
        mainSplitLayoutView.initShortcutsVESCurvesNavigation();
    }

    private void initAndShowWindow(String windowTitle, VBView<? extends ViewModel> view, ViewModel viewModel) {
        Stage stage = new Stage();
        stage.setScene(new Scene(view));
        stage.setTitle(windowTitle);
        stage.setWidth(1280);
        stage.setHeight(720);
        stage.show();
        viewModelStageMap.put(viewModel, stage);
    }

    public List<File> showOpenEXPFileChooser(ViewModel caller) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл полевых данных для интерпретации");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EXP - Полевые данные", "*.EXP", "*.exp")
        );
        return chooser.showOpenMultipleDialog(viewModelStageMap.get(caller));
//      если закрыть окно выбора файла, ничего не выбрав, то FileChooser вернет null
    }

    public File showOpenMODFileChooser(ViewModel caller) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл модели");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MOD - Данные модели", "*.MOD", "*.mod")
        );
        return chooser.showOpenDialog(viewModelStageMap.get(caller));
    }

    public void alertExperimentalDataIsUnsafe(ViewModel caller, String picketName) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(uiProperties.getString("compatibilityMode"));
        alert.setHeaderText(picketName + " - " + uiProperties.getString("EXPSTTMismatch"));
        alert.setContentText(uiProperties.getString("minimalDataWillBeDisplayed"));
        alert.initOwner(viewModelStageMap.get(caller));
        alert.getDialogPane().getStylesheets().add("ru/nucodelabs/gem/view/common.css");
        alert.show();
    }

    public void alertFileNotFound(ViewModel caller, FileNotFoundException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(uiProperties.getString("error"));
        alert.setHeaderText(uiProperties.getString("fileNotFound"));
        alert.setContentText(e.getMessage());
        alert.initOwner(viewModelStageMap.get(caller));
        alert.getDialogPane().getStylesheets().add("ru/nucodelabs/gem/view/common.css");
        alert.show();
    }

    public void alertNoLib(ViewModel caller, UnsatisfiedLinkError e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(uiProperties.getString("unableToDrawChart"));
        alert.setHeaderText(uiProperties.getString("noLib"));
        alert.setContentText(e.getMessage());
        alert.initOwner(viewModelStageMap.get(caller));
        alert.getDialogPane().getStylesheets().add("ru/nucodelabs/gem/view/common.css");
        alert.show();
    }

    public void alertIncorrectFile(ViewModel caller, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle("Неверный формат файла");
        alert.setHeaderText(uiProperties.getString("fileError"));
        alert.setContentText(e.getMessage());
        alert.initOwner(viewModelStageMap.get(caller));
        alert.getDialogPane().getStylesheets().add("ru/nucodelabs/gem/view/common.css");
        alert.show();
    }

    public File showSaveJsonFileChooser(ViewModel caller) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(uiProperties.getString("save") + " " + uiProperties.getString("section"));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("json", "*.json")
        );
        return chooser.showSaveDialog(viewModelStageMap.get(caller));
    }

    public File showOpenJsonFileChooser(ViewModel caller) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(uiProperties.getString("open") + " " + uiProperties.getString("section"));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("json", "*.json")
        );
        return chooser.showOpenDialog(viewModelStageMap.get(caller));
    }
}
