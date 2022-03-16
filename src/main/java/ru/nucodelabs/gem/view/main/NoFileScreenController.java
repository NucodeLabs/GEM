package ru.nucodelabs.gem.view.main;

import com.google.inject.name.Named;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.nucodelabs.gem.view.Controller;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class NoFileScreenController implements Controller {

    @Inject
    @Named("ImportEXP")
    private Runnable importEXP;
    @Inject
    @Named("OpenSection")
    private Runnable openSection;

    @FXML
    public VBox root;

    @FXML
    private void importEXP() {
        importEXP.run();
    }

    @FXML
    private void openSection() {
        openSection.run();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    public void hide() {
        root.setVisible(false);
    }
}
