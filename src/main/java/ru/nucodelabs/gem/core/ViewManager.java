package ru.nucodelabs.gem.core;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.nucodelabs.gem.view.main.ImportOptionsPrompt;
import ru.nucodelabs.gem.view.main.MainSplitLayoutView;
import ru.nucodelabs.gem.view.main.MainViewModel;
import ru.nucodelabs.mvvm.ViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * <h2>View Manager</h2>
 * Creates and opens Views and giving them references to their ViewModels linked with Models.
 * Also changing Views dynamically if ViewModel asks to do so.
 */
public class ViewManager {

    private final ViewModelFactory viewModelFactory;
    private final Stage initialStage;
    private final Map<ViewModel, Stage> viewModelStageMap;

    public ViewManager(ViewModelFactory viewModelFactory, Stage initialStage) {
        this.viewModelFactory = viewModelFactory;
        viewModelFactory.initViewManager(this);
        this.initialStage = initialStage;
        viewModelStageMap = new HashMap<>();
    }

    /**
     * Opens welcome window
     */
    public void start() {
        MainViewModel mainViewModel = viewModelFactory.createMainViewModel();
        viewModelStageMap.put(mainViewModel, initialStage);
        MainSplitLayoutView mainSplitLayoutView = new MainSplitLayoutView(mainViewModel);

        initialStage.setTitle("GEM");
        initialStage.getIcons().add(new Image("img/gem.png"));
        initialStage.setScene(new Scene(mainSplitLayoutView));
        initialStage.show();
        initialStage.setMaximized(true);
    }

    public void newMainViewWithImportEXP(ViewModel caller) {
        File expFile = showEXPFileChooser(caller);

        if (expFile != null) {
            MainViewModel mainViewModel = viewModelFactory.createMainViewModel();
            MainSplitLayoutView mainSplitLayoutView = new MainSplitLayoutView(mainViewModel);
            Stage newStage = new Stage();
            viewModelStageMap.put(mainViewModel, newStage);
            newStage.setScene(new Scene(mainSplitLayoutView));
            newStage.show();
            newStage.setMaximized(true);
            mainSplitLayoutView.initShortcutsVESCurvesNavigation();
            mainSplitLayoutView.getViewModel().addToCurrent(expFile);
        }
    }

    public File showEXPFileChooser(ViewModel caller) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл полевых данных для интерпретации");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EXP - Полевые данные", "*.EXP", "*.exp")
        );
        return chooser.showOpenDialog(viewModelStageMap.get(caller));
//      если закрыть окно выбора файла, ничего не выбрав, то FileChooser вернет null
    }

    public File showMODFileChooser(ViewModel caller) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл модели");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MOD - Данные модели", "*.MOD", "*.mod")
        );
        return chooser.showOpenDialog(viewModelStageMap.get(caller));
    }

    public void alertExperimentalDataIsUnsafe(ViewModel caller) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Режим совместимости");
        alert.setHeaderText("STT и EXP содержат разное количество строк");
        alert.setContentText("Будет отображаться минимально возможное число данных");
        alert.initOwner(viewModelStageMap.get(caller));
        alert.getDialogPane().getStylesheets().add("ru/nucodelabs/gem/view/common.css");
        alert.show();
    }

    public void alertFileNotFound(ViewModel caller, FileNotFoundException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Файл не найден!");
        alert.setContentText(e.getMessage());
        alert.initOwner(viewModelStageMap.get(caller));
        alert.getDialogPane().getStylesheets().add("ru/nucodelabs/gem/view/common.css");
        alert.show();
    }

    public void alertNoLib(ViewModel caller, UnsatisfiedLinkError e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Невозможно отрисовать график");
        alert.setHeaderText("Отсутствует библиотека");
        alert.setContentText(e.getMessage());
        alert.initOwner(viewModelStageMap.get(caller));
        alert.getDialogPane().getStylesheets().add("ru/nucodelabs/gem/view/common.css");
        alert.show();
    }

    public void alertIncorrectFile(ViewModel caller, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle("Неверный формат файла");
        alert.setHeaderText("Произошла ошибка при открытии файла");
        alert.setContentText(e.getMessage());
        alert.initOwner(viewModelStageMap.get(caller));
        alert.getDialogPane().getStylesheets().add("ru/nucodelabs/gem/view/common.css");
        alert.show();
    }

    public void askImportOption(ViewModel caller) {
        ImportOptionsPrompt importOptionsPrompt = new ImportOptionsPrompt((MainViewModel) caller);
        Stage newStage = new Stage();
        newStage.setScene(new Scene(importOptionsPrompt));
        newStage.initOwner(viewModelStageMap.get(caller));
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.setResizable(false);
        newStage.show();
    }
}
