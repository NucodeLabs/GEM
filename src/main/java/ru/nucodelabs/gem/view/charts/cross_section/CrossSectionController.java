package ru.nucodelabs.gem.view.charts.cross_section;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.files.color_palette.CLRData;
import ru.nucodelabs.gem.view.AbstractController;
import ru.nucodelabs.gem.view.color_pallete.ColorPalette;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CrossSectionController extends AbstractController {

    private final ObservableObjectValue<Section> section;
    private ColorPalette colorPalette;
    @FXML
    public NumberAxis sectionX;
    @FXML
    public NumberAxis sectionY;
    @FXML
    public AreaChart<Number, Number> sectionAreaChart;

    @Inject
    private ObjectProperty<ObservableList<XYChart.Series<Number, Number>>> dataProperty;

    @Inject
    private ObjectProperty<CLRData> clrDataProperty;

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
        colorPalette = new ColorPalette(clrDataProperty.get());
    }

    public void update() {
        List<XYChart.Series<Number, Number>> seriesList = CrossSectionConverters.makeSectionSeries(section.get().getPickets());

        dataProperty.get().setAll(FXCollections.observableArrayList(seriesList));
        updateSeriesColors(dataProperty.get());
        //updatePicketTooltips(dataProperty.get());
    }

    private void updateSeriesColors(List<XYChart.Series<Number, Number>> seriesList) {
        int count = 0;
        for (Picket picket : section.get().getPickets()) {
            String lastLayerStyle;
            if (picket.getModelData().size() > 0) {
                for (int i = 0; i < picket.getModelData().size(); i++) {
                    double resistance = picket.getModelData().get(i).getResistance();

                    //Assign color according to resistance value
                    //String layerColor = colorPalette.getRGBColor(resistance);
                    String layerColor = colorPalette.getCLRColor(resistance, 1500);

                    //Find positive part and color it
                    seriesList.get(count).getNode().lookup(".chart-series-area-fill")
                            .setStyle("-fx-fill: rgba(" + layerColor + ");");

                    //Set viewOrder of positive part more to background,
                    // because it is intended to be a higher area of the picket
                    // and should not overlap on lower layers
                    seriesList.get(count++).getNode().viewOrderProperty().setValue(picket.getModelData().size() - i);

                    //Find positive part and color it
                    seriesList.get(count).getNode().lookup(".chart-series-area-fill")
                            .setStyle("-fx-fill: rgba(" + layerColor + ");");

                    //Set viewOrder of positive part more to foreground,
                    // because it is intended to be a lower area of the picket
                    // and should not overlap on higher layers
                    seriesList.get(count++).getNode().viewOrderProperty().setValue(i);
                }

                //Extract infinite layer color
                lastLayerStyle = seriesList.get(count - 1).getNode().lookup(".chart-series-area-fill").getStyle();

                //Color shifting layer
                seriesList.get(count).getNode().lookup(".chart-series-area-fill")
                        .setStyle("-fx-fill: rgba(255, 255, 255, 1.0);");

                //Set shifting layers orderView in such way, that it will be showed only if picket is shifted completely below X axis
                if ((picket.zOfModelLayers().get(0) + picket.getModelData().get(0).getPower() < 0) ||
                        (picket.zOfModelLayers().get(picket.getModelData().size() - 1) - picket.getModelData().get(picket.getModelData().size() - 1).getPower() > 0)) {
                    seriesList.get(count).getNode().viewOrderProperty().setValue(-1);
                    seriesList.get(count++).getNode().setVisible(true);
                } else {
                    seriesList.get(count++).getNode().setVisible(false);
                }

                //Color the negative part of bottom layer. Positive part is drawn automatically using picket data.
                seriesList.get(count).getNode().lookup(".chart-series-area-fill")
                        .setStyle(lastLayerStyle);
                seriesList.get(count++).getNode().viewOrderProperty().setValue(picket.getModelData().size() + 1);
            }
        }
    }

    private void updatePicketTooltips(List<XYChart.Series<Number, Number>> seriesList) {

        for (XYChart.Series<Number, Number> series : seriesList) {
            for (XYChart.Data<Number, Number> data : series.getData()) {
                Tooltip tooltip = new Tooltip(series.getName());
                Tooltip.install(data.getNode(), tooltip);

                Point2D localPoint = data.getNode().localToScene(0, 0);

                tooltip.setX(localPoint.getX());
                tooltip.setY(localPoint.getY());

                data.getNode().setOnMouseEntered(event -> {
                    data.getNode().setStyle("-fx-background-color: ORANGE");
                    tooltip.show(getStage());
                });
                data.getNode().setOnMouseExited(event -> {
                    data.getNode().setStyle("-fx-background-color: transparent");
                    //tooltip.re;
                });
            }
        }
    }

    protected Stage getStage() {
        return (Stage) sectionAreaChart.getScene().getWindow();
    }
}
