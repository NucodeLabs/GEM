package ru.nucodelabs.gem.core;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.nucodelabs.gem.view.main.MainSplitLayoutView;
import ru.nucodelabs.gem.view.main.MainViewModel;
import ru.nucodelabs.gem.view.welcome.WelcomeView;
import ru.nucodelabs.gem.view.welcome.WelcomeViewModel;

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
        MainSplitLayoutView mainSplitLayoutView = new MainSplitLayoutView(
                new MainViewModel(modelFactory.getVesDataModel(), this)
        );
        Scene scene = new Scene(mainSplitLayoutView);
        if (mainSplitLayoutView.getViewModel().importEXP()) { // надо вынести файловый диалог в отдельный объект
            stage.hide();                                    // потому что оповещений об ошибках (alert) не видно
            stage.setResizable(true);                       // при переходе с экрана приветствия на главное окно;
            stage.setScene(scene);                         // использовать view model до появления сцены все же костыль...
            stage.show();                                 // и я вроде было пофиксил это убрав hide/close и show
            stage.setMaximized(true);                    // но тогда после setResizable(true) окно все равно нельзя было
        }                                               // потянуть за края для изменения размера хотя кнопка 'развернуть' работала...
    }

    public Stage getStage() {
        return stage;
    }
}
