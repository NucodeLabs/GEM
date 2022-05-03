package ru.nucodelabs.gem.view.charts.cross_section;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.view.AbstractController;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CrossSectionController extends AbstractController {

    private final ObservableObjectValue<Section> section;
    @FXML
    public NumberAxis sectionX;
    @FXML
    public NumberAxis sectionY;
    @FXML
    public AreaChart<Number, Number> sectionAreaChart;

    @Inject
    private ObjectProperty<ObservableList<XYChart.Series<Number, Number>>> dataProperty;

    @Inject
    public CrossSectionController(ObservableObjectValue<Section> section) {
        this.section = section;

        this.section.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                update();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //uiProperties = resources;
        sectionAreaChart.dataProperty().bind(dataProperty);

    }

    public void update() {
        List<XYChart.Series<Number, Number>> seriesList = CrossSectionConverters.makeResistanceSeries(section.get().getPickets(), 0.0);

        dataProperty.get().setAll(FXCollections.observableArrayList(seriesList));
        updateSeriesColors(dataProperty.get());
        //updateViewOrder();
    }

    private void updateSeriesColors(List<XYChart.Series<Number, Number>> seriesList) {
        int count = 0;
        for (Picket picket : section.get().getPickets()) {
            for (int i = 0; i < picket.getModelData().size(); i++) {
                double resistance = picket.getModelData().get(i).getResistance();
                String layerColor = getRGBColor(resistance);

                seriesList.get(count).getNode().lookup(".chart-series-area-fill")
                        .setStyle("-fx-stroke: rgba(0, 0, 0, 1.0);"
                                + "-fx-fill: rgba(" + layerColor + ", 1.0);");

                seriesList.get(count++).getNode().viewOrderProperty().setValue(picket.getModelData().size() - i);

                seriesList.get(count).getNode().lookup(".chart-series-area-fill")
                        .setStyle("-fx-stroke: rgba(0, 0, 0, 1.0);"
                                + "-fx-fill: rgba(" + layerColor + ", 1.0);");

                seriesList.get(count++).getNode().viewOrderProperty().setValue(i);
            }
            seriesList.get(count).getNode().lookup(".chart-series-area-fill")
                    .setStyle("-fx-stroke: rgba(0, 0, 0, 1.0);"
                            + "-fx-fill: rgba(255, 255, 255, 1.0);");

            if ((picket.zOfLayers().get(0) + picket.getModelData().get(0).getPower() < 0) ||
                    (picket.zOfLayers().get(picket.getModelData().size() - 1) - picket.getModelData().get(picket.getModelData().size() - 1).getPower() > 0)) {
                seriesList.get(count++).getNode().viewOrderProperty().setValue(-1);
            } else {
                seriesList.get(count++).getNode().viewOrderProperty().setValue(picket.getModelData().size() + 1);
            }
        }
    }

    public String getRGBColor(double resistance) {
        Color color = Color.WHITESMOKE;

        if (0 < resistance & resistance < 20) {
            color = Color.LIGHTGREEN;
        } else if (20 <= resistance & resistance < 50) {
            color = Color.GREEN;
        } else if (50 <= resistance & resistance < 100) {
            color = Color.OLIVE;
        } else if (100 <= resistance & resistance < 150) {
            color = Color.DARKGREEN;
        } else if (150 <= resistance & resistance < 200) {
            color = Color.GREENYELLOW;
        } else if (200 <= resistance & resistance < 250) {
            color = Color.LIGHTYELLOW;
        } else if (250 <= resistance & resistance < 300) {
            color = Color.YELLOW;
        } else if (300 <= resistance & resistance < 350) {
            color = Color.DARKGOLDENROD;
        } else if (350 <= resistance & resistance < 400) {
            color = Color.ORANGE;
        } else if (400 <= resistance & resistance < 450) {
            color = Color.DARKORANGE;
        } else if (450 <= resistance & resistance < 500) {
            color = Color.ORANGERED;
        } else if (500 <= resistance & resistance < 750) {
            color = Color.RED;
        } else if (750 <= resistance & resistance < 1000) {
            color = Color.DARKRED;
        } else if (1000 <= resistance & resistance < 2500) {
            color = Color.GRAY;
        } else if (2500 <= resistance) {
            color = Color.BLACK;
        }

        //color = resistanceToColor(resistance);

        return String.format("%d, %d, %d",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /*private Color resistanceToColor(double resistance) {
        Color newColor = Color.rgb(resistance % 255);
        return newColor;
    }*/

    protected Stage getStage() {
        return (Stage) sectionAreaChart.getScene().getWindow();
    }
}
