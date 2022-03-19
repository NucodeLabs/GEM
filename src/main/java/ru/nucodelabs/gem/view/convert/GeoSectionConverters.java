package ru.nucodelabs.gem.view.convert;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ves.Picket;

import java.util.ArrayList;
import java.util.List;

public class GeoSectionConverters {

    public static XYChart.Series<String, Double> getLayerOfPowers(List<Picket> pickets, int layerNum) {
        XYChart.Series<String, Double> layerOfLayerPowers = new XYChart.Series<>();
        ObservableList<XYChart.Data<String, Double>> obsPowers = new SimpleListProperty<>();
        List<Double> powerList = new ArrayList<>();
        for (Picket picket : pickets) {
            powerList.add(picket.modelData().power().get(layerNum));
        }

        for (Double layerPower : powerList) {
            //XYChart.Data<String, Double> temp = new XYChart.Data<>(((Integer)layerNum).toString(), layerPower);
            obsPowers.add(new XYChart.Data<>(((Integer)layerNum).toString(), layerPower));
        }

        layerOfLayerPowers.setData(obsPowers);

        return layerOfLayerPowers;
    }
}
