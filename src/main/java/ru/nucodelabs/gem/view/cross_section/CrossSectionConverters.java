package ru.nucodelabs.gem.view.cross_section;

import javafx.collections.FXCollections;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ves.ModelDataRow;
import ru.nucodelabs.data.ves.Picket;

import java.util.ArrayList;
import java.util.List;

public class CrossSectionConverters {

    public static List<XYChart.Series<Number, Number>> getLayersOfPower(List<Picket> pickets) {
        int maxLayer = getMaxLayerCount(pickets);
        List<XYChart.Series<Number, Number>> powerLayers = new ArrayList<>();
        List<String> categories = makeCategories(pickets);

        for (int i = 0; i < maxLayer; i++) {
            powerLayers.add(makeLayer(pickets, i, categories.get(i)));
        }

        return powerLayers;
    }

    public static XYChart.Series<Number, Number> makeLayer(List<Picket> pickets, int layerNum, String name) {
        XYChart.Series<Number, Number> layer = new XYChart.Series<>();
        layer.setName(name);
        double currentCoordinate = 0.0;
        double picketWidth = 100.0;
        for (Picket picket : pickets) {
            if (layerNum < picket.modelData().size()) {
                layer.getData().add(new XYChart.Data<>(
                        currentCoordinate,
                        0.0));
                layer.getData().add(new XYChart.Data<>(
                        currentCoordinate,
                        picket.modelData().getHeight().get(layerNum)));
                layer.getData().add(new XYChart.Data<>(
                        currentCoordinate + picketWidth,
                        picket.modelData().getHeight().get(layerNum)));
                layer.getData().add(new XYChart.Data<>(
                        currentCoordinate + picketWidth,
                        0.0));
            }
            currentCoordinate += picketWidth;
        }

        return layer;
    }

    public static List<XYChart.Series<Number, Number>> makeResistanceSeries(List<Picket> pickets) {
        List<XYChart.Series<Number, Number>> picketSeries = new ArrayList<>();
        double currentCoordinate = 0.0;
        double picketWidth = 100.0;
        for (Picket picket : pickets) {
            for (int i = 0; i < picket.modelData().getRows().size(); i++) {
                picketSeries.add(new XYChart.Series<>());
                //System.out.println(picket.name());
                picketSeries.get(i).setName(picket.name() + " - " + i);

                XYChart.Data<Number, Number> leftZero = new XYChart.Data<>(
                        currentCoordinate,
                        0.0);
                XYChart.Data<Number, Number> leftLineDot = new XYChart.Data<>(
                        currentCoordinate,
                        picket.modelData().getHeight().get(i));
                XYChart.Data<Number, Number> rightLineDot = new XYChart.Data<>(
                        currentCoordinate + picketWidth,
                        picket.modelData().getHeight().get(i));
                XYChart.Data<Number, Number> rightZero = new XYChart.Data<>(
                        currentCoordinate + picketWidth,
                        0.0);

                picketSeries.get(i).getData().addAll(
                        leftZero,
                        leftLineDot,
                        rightLineDot,
                        rightZero);

            }
            currentCoordinate += picketWidth;
        }

        return picketSeries;
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
