package ru.nucodelabs.gem.view.color_palette;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ru.nucodelabs.files.color_palette.CLRData;
import ru.nucodelabs.gem.utils.NumbersUtils;
import ru.nucodelabs.gem.view.AbstractController;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ColorPaletteController extends AbstractController {

    @FXML
    public Pane rootPane;
    @FXML
    public Pane palettePane;
    @FXML
    public Pane labelsPane;
    @FXML
    public TextField minResistanceTF;
    @FXML
    public TextField maxResistanceTF;
    @FXML
    public TextField precisionTF;

    public DoubleProperty minResistanceProperty;

    public DoubleProperty maxResistanceProperty;

    public IntegerProperty precisionProperty;

    private DoubleProperty coeff;

    @Inject
    private ObjectProperty<ColorPalette> colorPaletteProperty;

    private CLRData clrData;

    private List<Double> keyList;

    private List<Rectangle> rectangleList;
    private List<Label> labelList;

    @Override
    protected Stage getStage() {
        return (Stage) rootPane.getScene().getWindow();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        minResistanceTF.setText("0.0");
        maxResistanceTF.setText("1500.0");

        minResistanceProperty = new SimpleDoubleProperty(0.0);
        maxResistanceProperty = new SimpleDoubleProperty(1500.0);
        precisionProperty = new SimpleIntegerProperty(20);
        coeff = new SimpleDoubleProperty(1.0 / precisionProperty.get());

        colorPaletteProperty.get().minValueProperty().bind(minResistanceProperty);
        colorPaletteProperty.get().maxValueProperty().bind(maxResistanceProperty);

        clrData = colorPaletteProperty.get().getClrData();
        keyList = clrData.getColorMap().keySet().stream().toList();
        rectangleList = new ArrayList<>();
        labelList = new ArrayList<>();

        //rootPane.setPrefHeight(400);

        //palettePane.setPrefWidth(100);
       // palettePane.setPrefHeight(250);

       // labelsPane.setPrefHeight(250);

        drawPalette();
        updatePaletteView();
    }

    private void drawPalette() {
        double prevKey = 0.0;
        for (int i = 0; i <= precisionProperty.get(); i++) {
            double key = i * coeff.get();
            Label label = new Label(String.valueOf(computeResistance(key)));
            label.setLayoutX(0);
            label.setLayoutY(labelsPane.getPrefHeight() * (key - 0.6 * coeff.get()));
            label.setUnderline(true);
            label.fontProperty().set(new Font(10));
            labelList.add(label);

            if (i == 0) {
                continue;
            } else {
                rectangleList.add(
                        new Rectangle(
                                0,
                                palettePane.getPrefHeight() * prevKey,
                                palettePane.getPrefWidth(),
                                palettePane.getPrefHeight() * (key - prevKey)));
            }

            prevKey = key;
        }

        palettePane.getChildren().addAll(rectangleList);
        labelsPane.getChildren().addAll(labelList);
    }

    private void updatePaletteView() {

        for (int i = 0; i <= precisionProperty.get(); i++) {
            double key = i * coeff.get();
            labelList.get(i).setText(
                    String.valueOf(
                            NumbersUtils.round(computeResistance(key), 2)));
            if (i < precisionProperty.get()) {
                Stop[] stops = {
                        new Stop(0, colorPaletteProperty.get().colorForValue(computeResistance(key))),
                        new Stop(1, colorPaletteProperty.get().colorForValue(computeResistance(key + coeff.get())))};
                LinearGradient linearGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);

                ((Rectangle) palettePane.getChildren().get(i)).setFill(linearGradient);
                palettePane.getChildren().get(i).setStyle("-fx-stroke: BLACK");
                //((Rectangle) palettePane.getChildren().get(i)).setWidth(rootVBox.getParent().getWidth());
            }
        }
    }

    private double computeResistance(double key) {
        return key * (maxResistanceProperty.get() - minResistanceProperty.get()) + minResistanceProperty.get();
    }

    @FXML
    public void onMinResistanceEdit() {
        minResistanceProperty.setValue(checkInputDouble(minResistanceTF.getText(), minResistanceTF, minResistanceProperty));
        updatePaletteView();
    }

    @FXML
    public void onMaxResistanceEdit() {
        maxResistanceProperty.setValue(checkInputDouble(maxResistanceTF.getText(), maxResistanceTF, maxResistanceProperty));
        updatePaletteView();
    }

    @FXML
    public void onPrecisionEdit() {
        precisionProperty.setValue(checkInputInteger(precisionTF.getText(), precisionTF, precisionProperty));
        coeff.set(1.0 / precisionProperty.get());

        rectangleList.clear();
        palettePane.getChildren().clear();

        labelList.clear();
        labelsPane.getChildren().clear();

        drawPalette();
        updatePaletteView();
    }

    private Double checkInputDouble(String input, TextField textField, DoubleProperty property) {
        double num;
        try {
            num = Double.parseDouble(input);
            if (num <= 0) {
                textField.setText(Double.toString(property.get()));
                return property.get();
            }
        } catch (NumberFormatException exc) {
            textField.setText(Double.toString(property.get()));
            return property.get();
        }

        return num;
    }

    private Integer checkInputInteger(String input, TextField textField, IntegerProperty property) {
        int num;
        try {
            num = Integer.parseInt(input);
            if (num <= 0) {
                textField.setText(Integer.toString(property.get()));
                return property.get();
            }
        } catch (NumberFormatException exc) {
            textField.setText(Integer.toString(property.get()));
            return property.get();
        }

        return num;
    }
}
