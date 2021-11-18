package ru.nucodelabs.gem;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.STTFile;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.log10;

public class InaccuracyCurve {

    protected static void makeCurve(LineChart<Double, Double> inaccuracyCurve, STTFile openedSTT, EXPFile openedEXP) {
        inaccuracyCurve.getData().clear();
        inaccuracyCurve.getData().addAll(makeCurveData(openedSTT, openedEXP));
        inaccuracyCurve.setCreateSymbols(false);
    }

    protected static List<XYChart.Series<Double, Double>> makeCurveData(STTFile openedSTT, EXPFile openedEXP) {
        List<XYChart.Series<Double, Double>> res = new ArrayList<>();

        for (int i = 0; i < openedSTT.getAB_2().size(); i++) {
            XYChart.Series<Double, Double> pointsSeries = new XYChart.Series<>();
            pointsSeries.getData().add(new XYChart.Data<Double, Double>(
                    log10(openedSTT.getAB_2().get(i)),
                    0d));
            double dotX = log10(openedSTT.getAB_2().get(i));
            double dotY = openedEXP.getErrorResistanceApp().get(i);
            pointsSeries.getData().add(new XYChart.Data<>(dotX, dotY));
            res.add(pointsSeries);
        }

        return res;
    }
}
