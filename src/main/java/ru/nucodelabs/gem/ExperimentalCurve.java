package ru.nucodelabs.gem;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.STTFile;

import static java.lang.Math.log10;

public class ExperimentalCurve {
    protected static void makeCurve(LineChart vesCurve, STTFile openedSTT, EXPFile openedEXP) {
        vesCurve.getData().clear();
        vesCurve.getData().addAll(makeCurveData(openedSTT, openedEXP));
    }

    protected static XYChart.Series makeCurveData(STTFile openedSTT, EXPFile openedEXP) {
        XYChart.Series pointsSeries = new XYChart.Series<>();

        for (int i = 0; i < openedSTT.getAB_2().size(); i++) {
            pointsSeries.getData().add(
                    new XYChart.Data<>(
                            log10(openedSTT.getAB_2().get(i)),
                            log10(openedEXP.getResistanceApp().get(i))));
        }

        return pointsSeries;
    }
}
