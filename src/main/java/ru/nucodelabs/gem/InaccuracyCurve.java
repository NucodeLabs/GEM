package ru.nucodelabs.gem;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.STTFile;

import static java.lang.Math.log10;

public class InaccuracyCurve {

    protected static void makeCurve(LineChart<Double, Double> inaccuracyCurve, STTFile openedSTT, EXPFile openedEXP) {
        inaccuracyCurve.getData().clear();
        inaccuracyCurve.getData().addAll(makeCurveData(openedSTT, openedEXP));
    }

    protected static XYChart.Series<Double, Double> makeCurveData(STTFile openedSTT, EXPFile openedEXP) {
        XYChart.Series<Double, Double> pointsSeries = new XYChart.Series<>();

        for (int i = 0; i < openedSTT.getAB_2().size(); i++) {
            pointsSeries.getData().add(
                    new XYChart.Data<>(
                            log10(openedSTT.getAB_2().get(i)),
                            openedEXP.getErrorResistanceApp().get(i)));
        }

        return pointsSeries;
    }
}
