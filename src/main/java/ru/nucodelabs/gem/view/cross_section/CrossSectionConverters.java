package ru.nucodelabs.gem.view.cross_section;

import javafx.collections.FXCollections;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ves.Picket;

import java.util.ArrayList;
import java.util.List;

public class CrossSectionConverters {

    public static List<XYChart.Series<String, Number>> getLayersOfPowers(List<Picket> pickets) {
        int maxLayer = getMaxLayerCount(pickets);
        List<XYChart.Series<String, Number>> powerLayers = new ArrayList<>(maxLayer);

        for (int i = 0; i < maxLayer; i++) {
            powerLayers.add(new XYChart.Series<>());
        }

        for (int i = 0; i < maxLayer; i++) {
            for (Picket picket : pickets) {
                if (picket.modelData() != null) {
                    if (i < picket.modelData().getRows().size()) {
                        powerLayers.get(i).getData().add(new XYChart.Data<>(
                                ((Integer) i).toString(),
                                picket.modelData().power().get(i)));
                    } else {
                        powerLayers.get(i).getData().add(new XYChart.Data<>(
                                ((Integer) i).toString(),
                                0.0));
                    }
                } else {
                    powerLayers.get(i).getData().add(new XYChart.Data<>(
                            ((Integer) i).toString(),
                            0.0));
                }

            }
        }

        return powerLayers;
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
            categories.add(((Integer)i).toString());
        }

        return categories;
    }
}
