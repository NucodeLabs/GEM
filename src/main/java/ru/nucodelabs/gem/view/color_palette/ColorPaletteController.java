package ru.nucodelabs.gem.view.color_palette;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.nucodelabs.gem.utils.NumbersUtils;
import ru.nucodelabs.gem.view.AbstractController;
import ru.nucodelabs.gem.view.color.ColorMapper;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ColorPaletteController extends AbstractController {

    @FXML
    private ContextMenu contextMenu;
    @FXML
    private Stage configWindow;
    @FXML
    private Pane palettePane;
    @FXML
    private Pane labelsPane;
    @FXML
    private TextField minResistanceTF;
    @FXML
    private TextField maxResistanceTF;
    @FXML
    private TextField precisionTF;

    public DoubleProperty minResistanceProperty;

    public DoubleProperty maxResistanceProperty;

    public IntegerProperty precisionProperty;

    private DoubleProperty coeff;

    @Inject
    private ColorMapper colorPalette;

    @Inject
    @Named("CSS")
    private String css;

    private List<Rectangle> rectangleList;
    private List<Label> labelList;

    @Override
    protected Stage getStage() {
        return (Stage) palettePane.getScene().getWindow();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configWindow.initStyle(StageStyle.UTILITY);
        configWindow.getScene().getStylesheets().add(css);

        minResistanceTF.setText("0.0");
        maxResistanceTF.setText("1500.0");

        minResistanceProperty = new SimpleDoubleProperty(0.0);
        maxResistanceProperty = new SimpleDoubleProperty(1500.0);
        precisionProperty = new SimpleIntegerProperty(15);
        coeff = new SimpleDoubleProperty(1.0 / precisionProperty.get());

        colorPalette.minValueProperty().bind(minResistanceProperty);
        colorPalette.maxValueProperty().bind(maxResistanceProperty);
        colorPalette.blocksCountProperty().bind(precisionProperty);

        rectangleList = new ArrayList<>();
        labelList = new ArrayList<>();

        drawPalette();
        updatePaletteView();
    }

    private void drawPalette2() {

    }

    private void drawPalette() {
        double prevKey = 0.0;
        for (int i = 0; i <= precisionProperty.get(); i++) {
            double key = i * coeff.get();
            Label label = new Label(String.valueOf(computeResistance(key)));
            label.setLayoutX(0);
            label.layoutYProperty().bind(labelsPane.heightProperty().multiply(key - 0.6 * coeff.get()));
            label.fontProperty().set(new Font(10));
            labelList.add(label);

            if (i == 0) {
                continue;
            } else {
                final double finalPrevKey = prevKey;
                rectangleList.add(
                        new Rectangle() {{
                            setX(0);
                            yProperty().bind(palettePane.heightProperty().multiply(finalPrevKey));
                            widthProperty().bind(palettePane.widthProperty().subtract(5));
                            heightProperty().bind(palettePane.heightProperty().multiply(key - finalPrevKey));
                        }});
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

                ((Rectangle) palettePane.getChildren().get(i)).setFill(colorPalette.getColorBlocks().get(i).getColor());
                palettePane.getChildren().get(i).setStyle("-fx-stroke: BLACK");
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
        if (precisionProperty.get() == 0) {
            coeff.set(1);
        } else {
            coeff.set(1.0 / precisionProperty.get());
        }

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
            if (num < 0) {
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
            if (num < 0) {
                textField.setText(Integer.toString(property.get()));
                return property.get();
            }
        } catch (NumberFormatException exc) {
            textField.setText(Integer.toString(property.get()));
            return property.get();
        }

        return num;
    }

    @FXML
    public void showContextMenu(ContextMenuEvent contextMenuEvent) {
        contextMenu.show(getStage(), contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
    }

    @FXML
    public void showConfigWindow(ActionEvent actionEvent) {
        if (configWindow.getOwner() == null) {
            configWindow.initOwner(getStage());
        }
        configWindow.show();
    }
}
