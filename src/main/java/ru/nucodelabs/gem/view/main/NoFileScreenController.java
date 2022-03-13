package ru.nucodelabs.gem.view.main;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.nucodelabs.gem.view.Controller;

import java.net.URL;
import java.util.ResourceBundle;

public class NoFileScreenController extends Controller {

    private Runnable importEXP = () -> System.out.println(this.getClass() + ": importEXP action not specified!");
    private Runnable openSection = () -> System.out.println(this.getClass() + ": openSection action not specified!");

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
    protected Stage getStage() {
        return (Stage) root.getScene().getWindow();
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
