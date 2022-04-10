package ru.nucodelabs.gem.view.cross_section;

import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ves.Picket;

import java.util.ArrayList;
import java.util.List;

public class CrossSectionConverters {

    /*public static List<XYChart.Series<String, Number>> getPicketBars(List<Picket> pickets) {
        List<XYChart.Series<String,Number>> picketSeries = new ArrayList<>();
        int maxLayer = getMaxLayerCount(pickets);
        List<String> categories = makeCategories(pickets);

        for (Picket picket : pickets) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (int i = 0; i < maxLayer; i++) {

                series.getData().add(new XYChart.Data<>())
            }
        }
    }*/

    public static List<XYChart.Series<String, Number>> getLayersOfPowers(List<Picket> pickets) {
        int maxLayer = getMaxLayerCount(pickets);
        List<XYChart.Series<String, Number>> powerLayers = new ArrayList<>(maxLayer);
        List<String> categories = makeCategories(pickets);

        for (int i = 0; i < maxLayer; i++) {
            powerLayers.add(new XYChart.Series<>());
        }

        for (int i = 0; i < maxLayer; i++) {
            for (Picket picket : pickets) {
                powerLayers.get(i).setName(categories.get(i));
                if (picket.modelData() != null) {
                    if (i < picket.modelData().getRows().size()) {
                        powerLayers.get(i).getData().add(new XYChart.Data<>(
                                picket.name(),
                                picket.modelData().power().get(i)));
                    } else {
                        powerLayers.get(i).getData().add(new XYChart.Data<>(
                                picket.name(),
                                0.0));
                    }
                } else {
                    powerLayers.get(i).getData().add(new XYChart.Data<>(
                            picket.name(),
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
            categories.add(((Integer) i).toString());
        }

        System.out.println(categories);
        return categories;
    }
}
