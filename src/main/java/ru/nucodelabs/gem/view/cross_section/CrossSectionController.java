package ru.nucodelabs.gem.view.cross_section;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.view.AbstractController;

import javax.inject.Inject;
import java.net.URL;
import java.util.HexFormat;
import java.util.List;
import java.util.ResourceBundle;

public class CrossSectionController extends AbstractController {

    private final ObservableList<Picket> picketObservableList;
    public int picketCount;
    @FXML
    public NumberAxis sectionX;
    @FXML
    public NumberAxis sectionY;
    @FXML
    public AreaChart<Number, Number> sectionAreaChart;

    @Inject
    private ObjectProperty<ObservableList<XYChart.Series<Number, Number>>> dataProperty;

    @Inject
    public CrossSectionController(ObservableList<Picket> picketObservableList) {
        this.picketObservableList = picketObservableList;

        this.picketObservableList.addListener((ListChangeListener<Picket>) c -> {
            if (c.next()) {
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
        //List<XYChart.Series<Number, Number>> seriesList = CrossSectionConverters.getLayersOfPower(picketObservableList);
        List<XYChart.Series<Number, Number>> seriesList = CrossSectionConverters.makeResistanceSeries(picketObservableList);

        dataProperty.get().setAll(FXCollections.observableArrayList(seriesList));
        //System.out.println(dataProperty.get());
        updateSeriesColors(dataProperty.get());

    }

    private void updateSeriesColors(List<XYChart.Series<Number, Number>> seriesList) {
        for (Picket picket : picketObservableList) {
            for (int i = 0; i < picket.modelData().getRows().size(); i++) {
                double resistance = picket.modelData().getRows().get(i).resistance();
                String layerColor = getRGBColor(resistance);
                System.out.println(resistance + " - " + layerColor);
                seriesList.get(i).getNode().setStyle("-fx-stroke: rgb(128,128,128);"
                        + "-fx-fill: rgb(128,128,128);"
                        + "-fx-background-color: rgb(128,128,128);"
                        + "-fx-opacity: 0.2;");
                //seriesList.get(i).getNode().setStyle("-fx-stroke: 0.5");

                //seriesList.get(i).getNode().setStyle("-fx-stroke: rgba(" + layerColor + ", 0.0);");


                //seriesList.get(i).getNode().setStyle("-fx-fill: rgba(" + layerColor + ", 0.0);");
                // seriesList.get(i).getNode().setStyle("-fx-stroke: rgba(" + layerColor + ", 0.0);");
            }
        }
    }

    private void updateLayersColors(List<XYChart.Series<Number, Number>> seriesList) {
        for (int i = 0; i < seriesList.size(); i++) {
            XYChart.Series<Number, Number> series = seriesList.get(i);
            for (int j = 0; j < series.getData().size(); j++) {
                XYChart.Data<Number, Number> picketLine = series.getData().get(j);
                ModelData modelData = picketObservableList.get(j / 4).modelData();
                if (i < modelData.getRows().size()) {
                    double resistance = modelData.resistance().get(i);
                    String layerColor = getRGBColor(resistance);

                    picketLine.getNode().setStyle("-fx-fill: rgba(" + layerColor + ", 1.0);");
                    picketLine.getNode().setStyle("-fx-border-color: rgba(" + layerColor + ", 1.0);");

                    //picketLine.getNode().setStyle("-fx-background-color:" + layerColor);
                    //picketLine.getNode().setStyle("-fx-opacity: 0.0");
                }
            }
        }
    }

    public String getRGBColor(double resistance) {
        Color color = Color.WHITESMOKE;

        if (0 < resistance & resistance < 20) {
            color = Color.LIGHTGREEN; //"#90EE90"
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

        return String.format("%d, %d, %d",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    protected Stage getStage() {
        return (Stage) sectionAreaChart.getScene().getWindow();
    }

    public int getPicketCount() {
        return picketCount;
    }
}
