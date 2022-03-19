package ru.nucodelabs.gem.view.convert;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ves.Picket;

import java.util.ArrayList;
import java.util.List;

public class CrossSectionConverters {

    public static XYChart.Series<String, Double> getLayerOfPowers(List<Picket> pickets, int layerNum) {
        XYChart.Series<String, Double> layerOfLayerPowers = new XYChart.Series<>();
        //ObservableList<XYChart.Data<String, Double>> obsPowers = new ;
        List<Double> powerList = new ArrayList<>();

        for (Picket picket : pickets) {
            if (picket.modelData() != null) {
                powerList.add(picket.modelData().power().get(layerNum));
            } else {
                powerList.add(10.0);
            }
        }

        for (Double aDouble : powerList) {
            //XYChart.Data<String, Double> temp = new XYChart.Data<>(((Integer)layerNum).toString(), layerPower);
            String layerNumStr = ((Integer) layerNum).toString();
            double layerPower = aDouble;

            XYChart.Data<String, Double> pair = new XYChart.Data<>(layerNumStr, layerPower);

            layerOfLayerPowers.getData().add(pair);
        }

        //layerOfLayerPowers.setData(obsPowers);

        return layerOfLayerPowers;
    }
}
