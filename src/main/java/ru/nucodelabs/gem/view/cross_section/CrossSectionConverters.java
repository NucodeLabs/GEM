package ru.nucodelabs.gem.view.cross_section;

import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ves.Picket;

import java.util.ArrayList;
import java.util.List;

public class CrossSectionConverters {

    public static List<XYChart.Series<String, Number>> getLayersOfPower(List<Picket> pickets) {
        int maxLayer = getMaxLayerCount(pickets);
        List<XYChart.Series<String, Number>> powerLayers = new ArrayList<>();
        List<String> categories = makeCategories(pickets);

        for (int i = 0; i < maxLayer; i++) {
            powerLayers.add(makeLayer(pickets, i, categories.get(i)));
        }

        return powerLayers;
    }

    public static XYChart.Series<String, Number> makeLayer(List<Picket> pickets, int layerNum, String name) {
        XYChart.Series<String, Number> layer = new XYChart.Series<>();
        layer.setName(name);
        for (Picket picket : pickets) {
            if (layerNum < picket.modelData().size()) {
                layer.getData().add(new XYChart.Data<>(picket.name(), picket.modelData().power().get(layerNum)));
            }
        }

        return layer;
    }

    public static int getMaxLayerCount(List<Picket> pickets) {
        int maxLayer = 0;
        for (Picket picket : pickets) {
            if (picket.modelData() != null) {
                if (maxLayer < picket.modelData().getRows().size()) {
                    maxLayer = picket.modelData().getRows().size();
                }
            }
        }

        return maxLayer;
    }

    public static List<String> makeCategories(List<Picket> pickets) {
        List<String> categories = new ArrayList<>();
        int layers = getMaxLayerCount(pickets);

        for (int i = 0; i < layers; i++) {
            categories.add(((Integer) i).toString());
        }

        return categories;
    }
}
