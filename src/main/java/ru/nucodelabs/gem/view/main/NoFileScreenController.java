package ru.nucodelabs.gem.view.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.nucodelabs.gem.view.Controller;

public class NoFileScreenController extends Controller {

    private Runnable importEXP;
    private Runnable openSection;

    @FXML
    public VBox root;
    @FXML
    private Button openEXPButton;
    @FXML
    private Button openSectionButton;

    @FXML
    private void importEXP() {
        importEXP.run();
    }

    @FXML
    private void openSection() {
        openSection.run();
    }

    @Override
    protected Stage getStage() {
        return (Stage) openEXPButton.getScene().getWindow();
    }

    public void hide() {
        root.setVisible(false);
    }

    public void setImportEXPAction(Runnable importEXP) {
        this.importEXP = importEXP;
    }

    public void setOpenSectionAction(Runnable openSection) {
        this.openSection = openSection;
    }
}
