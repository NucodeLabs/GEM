package ru.nucodelabs.algorithms.charts;

import ru.nucodelabs.algorithms.forward_solver.ForwardSolver;
import ru.nucodelabs.data.ves.ExperimentalMeasurement;
import ru.nucodelabs.data.ves.ModelLayer;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.signum;

final class MisfitValuesFactoryNative implements MisfitValuesFactory {

    @Override
    public List<Double> apply(List<ExperimentalMeasurement> experimentalData, List<ModelLayer> modelData) {
        List<Double> resistanceApparent = experimentalData.stream().map(ExperimentalMeasurement::getResistanceApparent).toList();
        List<Double> errorResistanceApparent = experimentalData.stream().map(ExperimentalMeasurement::geErrorResistanceApparent).toList();

        if (experimentalData.size() == 0 || modelData.size() == 0) {
            return new ArrayList<>();
        }

        ForwardSolver forwardSolver = ForwardSolver.createDefaultForwardSolver(experimentalData, modelData);

        List<Double> solvedResistance = forwardSolver.solve();

        List<Double> res = new ArrayList<>();

        for (int i = 0; i < experimentalData.size(); i++) {
            double value = abs(MisfitFunctions.calculateRelativeDeviationWithError(
                    resistanceApparent.get(i),
                    errorResistanceApparent.get(i) / 100f,
                    solvedResistance.get(i)
            )) * signum(solvedResistance.get(i) - resistanceApparent.get(i)) * 100f;
            res.add(value);
        }

        return res;
    }
}
