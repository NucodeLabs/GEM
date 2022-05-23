package ru.nucodelabs.gem.view.color_palette;

import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.nucodelabs.gem.view.AbstractController;

import javax.inject.Inject;
import javax.inject.Named;
import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ColorPaletteController extends AbstractController {

    @FXML
    public Canvas palette;
    @FXML
    public TextField minResistanceTF;
    @FXML
    public TextField maxResistanceTF;

    @Inject
    @Named
    public DoubleProperty minResistanceProperty;

    @Inject
    public DoubleProperty maxResistanceProperty;

    @Override
    protected Stage getStage() {
        return null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    public void onMinResistanceEdit() {
        minResistanceProperty.setValue(Integer.parseInt(minResistanceTF.getText()));
    }

    @FXML
    public void onMaxResistanceEdit() {
        maxResistanceProperty.setValue(Integer.parseInt(maxResistanceTF.getText()));
    }
}
