package ru.nucodelabs.gem.view;

import javafx.scene.chart.XYChart;
import ru.nucodelabs.algorithms.ForwardSolver;
import ru.nucodelabs.algorithms.MisfitFunctions;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class MisfitStacksSeriesConverter {
    public List<XYChart.Series<Double, Double>> toMisfitStacksSeriesList(final ExperimentalData experimentalData, final ModelData modelData) {
        final List<Double> resistance = modelData.getResistance();
        final List<Double> power = modelData.getPower();
        final List<Double> ab_2 = experimentalData.getAB_2();
        final List<Double> resistanceApparent = experimentalData.getResistanceApparent();
        final List<Double> errorResistanceApparent = experimentalData.getErrorResistanceApparent();
        final int size = experimentalData.getSize();

        ArrayList<Double> solvedResistance = new ArrayList<>(ForwardSolver.ves(
                resistance,
                power,
                ab_2
        ));

        List<XYChart.Series<Double, Double>> res = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            XYChart.Series<Double, Double> pointsSeries = new XYChart.Series<>();
            pointsSeries.getData().add(new XYChart.Data<>(
                    log10(ab_2.get(i)),
                    0d));
            double dotX = log10(ab_2.get(i));
            double dotY = abs(MisfitFunctions.calculateRelativeDeviationWithError(
                    resistanceApparent.get(i),
                    errorResistanceApparent.get(i) / 100f,
                    solvedResistance.get(i)
            )) * signum(solvedResistance.get(i) - resistanceApparent.get(i)) * 100f;
            pointsSeries.getData().add(new XYChart.Data<>(dotX, dotY));
            res.add(pointsSeries);
        }

        return res;
    }
}
