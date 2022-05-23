package ru.nucodelabs.gem.view.color_palette;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import ru.nucodelabs.gem.view.AbstractController;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ColorPaletteController extends AbstractController {

    @FXML
    public Canvas palette;


    @Override
    protected Stage getStage() {
        return null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
