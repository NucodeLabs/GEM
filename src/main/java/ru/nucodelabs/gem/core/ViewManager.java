package ru.nucodelabs.gem.core;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.nucodelabs.gem.view.main.MainSplitLayoutView;
import ru.nucodelabs.gem.view.main.MainViewModel;
import ru.nucodelabs.gem.view.main.WelcomeView;
import ru.nucodelabs.gem.view.main.WelcomeViewModel;

/**
 * <h2>View Manager</h2>
 * Creates and opens Views and giving them references to their ViewModels linked with Models.
 * Also changing dynamically Views if ViewModel required to.
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
        stage.show();
    }

    public void openMainViewWithImport() {
        MainSplitLayoutView mainSplitLayoutView = new MainSplitLayoutView(
                new MainViewModel(modelFactory.getVesDataModel(), this)
        );
        Scene scene = new Scene(mainSplitLayoutView);
        if (mainSplitLayoutView.getViewModel().importEXPSTT(stage.getScene())) {
            stage.close();
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();
        }
    }
}
