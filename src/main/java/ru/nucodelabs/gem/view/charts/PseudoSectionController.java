package ru.nucodelabs.gem.view.charts;

import javafx.beans.value.ObservableObjectValue;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.view.AbstractController;
import ru.nucodelabs.gem.view.color_palette.ColorPalette;
import ru.nucodelabs.gem.view.usercontrols.heatmap.HeatMap;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PseudoSectionController extends AbstractController {

    @FXML
    private HeatMap chart;
    @Inject
    private ObservableObjectValue<Section> sectionObservableObjectValue;
    @Inject
    private ColorPalette colorPalette;

    @Override
    protected Stage getStage() {
        return null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chart.setColorPalette(colorPalette);
        sectionObservableObjectValue.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                update();
            }
        });
    }

    private void update() {
        var section = sectionObservableObjectValue.get();
        List<XYChart.Data<Double, Double>> data = new ArrayList<>();
        for (var picket : section.getPickets()) {
            for (var expData : picket.getExperimentalData()) {
                data.add(new XYChart.Data<>(
                        section.xOfPicket(picket),
                        expData.getAb2(),
                        expData.getResistanceApparent()));
            }
        }
        chart.getData().setAll(data);
    }
}
