package ru.nucodelabs.gem.view.color_palette;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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

    private List<Rectangle> rectangleList;

    @Override
    protected Stage getStage() {
        return (Stage)rootHBox.getScene().getWindow();
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
        rectangleList = new ArrayList<>();

        palettePane.setPrefWidth(rootHBox.getWidth());
        palettePane.setPrefHeight(rootHBox.getHeight());

        drawPalette();
        updatePaletteView();
    }

    private void drawPalette() {
        List<Double> keyList = clrData.colorMap.keySet().stream().toList();
        double prevKey = 0.0;
        for (int i = 0; i < clrData.colorMap.keySet().size(); i++) {
            double key = keyList.get(i);
            if (i == 0) {
                continue;
            } else {
                rectangleList.add(          //AAAAAAAA
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
        palettePane.getChildren().forEach(rect -> {
            Stop[] stops = {new Stop(0, Color.BLACK), new Stop(1, Color.RED)};
            LinearGradient linearGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
            ((Rectangle) rect).setFill(linearGradient);
            rect.setStyle("-fx-stroke: BLACK");
        });
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
                System.out.println("Wrong input! < 0");
                return resistanceProperty.get();
            }
        } catch (NumberFormatException exc) {
            textField.setText(Double.toString(resistanceProperty.get()));
            System.out.println("Wrong input! Not number");
            return resistanceProperty.get();
        }

        return num;
    }
}
