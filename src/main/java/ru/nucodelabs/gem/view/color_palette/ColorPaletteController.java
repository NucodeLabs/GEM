package ru.nucodelabs.gem.view.color_palette;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import ru.nucodelabs.files.color_palette.CLRData;
import ru.nucodelabs.gem.view.AbstractController;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ColorPaletteController extends AbstractController {

    @FXML
    public HBox rootHBox;
    @FXML
    public Pane palettePane;
    @FXML
    public TextField minResistanceTF;
    @FXML
    public TextField maxResistanceTF;

    public DoubleProperty minResistanceProperty;

    public DoubleProperty maxResistanceProperty;

    @Inject
    private ObjectProperty<ColorPalette> colorPaletteProperty;

    private CLRData clrData;

    private List<Double> keyList;

    private List<Rectangle> rectangleList;

    @Override
    protected Stage getStage() {
        return (Stage) rootHBox.getScene().getWindow();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        minResistanceTF.setText("0.0");
        maxResistanceTF.setText("1500.0");

        minResistanceProperty = new SimpleDoubleProperty(0.0);
        maxResistanceProperty = new SimpleDoubleProperty(1500.0);

        colorPaletteProperty.get().minValueProperty().bind(minResistanceProperty);
        colorPaletteProperty.get().maxValueProperty().bind(maxResistanceProperty);

        clrData = colorPaletteProperty.get().getClrData();
        keyList = clrData.colorMap.keySet().stream().toList();
        rectangleList = new ArrayList<>();

        palettePane.setPrefWidth(rootHBox.getWidth());
        palettePane.setPrefHeight(rootHBox.getHeight());

        drawPalette();
        updatePaletteView();
    }

    private void drawPalette() {
        double prevKey = 0.0;
        for (int i = 0; i < clrData.colorMap.keySet().size(); i++) {
            double key = keyList.get(i);
            if (i == 0) {
                continue;
            } else {
                rectangleList.add(
                        new Rectangle(
                                0.0,
                                300 * prevKey / 100,
                                150,
                                300 * (key - prevKey) / 100));
            }

            prevKey = key;
        }

        palettePane.getChildren().addAll(rectangleList);
    }

    private void updatePaletteView() {

        for (int i = 0; i < keyList.size(); i++) {
            if (i < keyList.size() - 1) {
                Stop[] stops = {
                        new Stop(0, colorPaletteProperty.get().colorForValue(keyList.get(i) * (maxResistanceProperty.get() - minResistanceProperty.get()) / 100 + minResistanceProperty.get())),
                        new Stop(1, colorPaletteProperty.get().colorForValue(keyList.get(i + 1) * (maxResistanceProperty.get() - minResistanceProperty.get()) / 100 + minResistanceProperty.get()))};
                LinearGradient linearGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);

                ((Rectangle) palettePane.getChildren().get(i)).setFill(linearGradient);
                palettePane.getChildren().get(i).setStyle("-fx-stroke: BLACK");
            }
        }
    }

    @FXML
    public void onMinResistanceEdit() {
        minResistanceProperty.setValue(checkInput(minResistanceTF.getText(), minResistanceTF, minResistanceProperty));
        updatePaletteView();
    }

    @FXML
    public void onMaxResistanceEdit() {
        maxResistanceProperty.setValue(checkInput(maxResistanceTF.getText(), maxResistanceTF, maxResistanceProperty));
        updatePaletteView();
    }

    private Double checkInput(String input, TextField textField, DoubleProperty resistanceProperty) {
        double num;
        try {
            num = Double.parseDouble(input);
            if (num < 0) {
                textField.setText(Double.toString(resistanceProperty.get()));
                return resistanceProperty.get();
            }
        } catch (NumberFormatException exc) {
            textField.setText(Double.toString(resistanceProperty.get()));
            return resistanceProperty.get();
        }

        return num;
    }
}
