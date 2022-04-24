package ru.nucodelabs.algorithms.charts;

import ru.nucodelabs.algorithms.forward_solver.ForwardSolver;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelLayer;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.signum;

final class MisfitValuesFactoryNative implements MisfitValuesFactory {

    @Override
    public List<Double> apply(List<ExperimentalData> experimentalData, List<ModelLayer> modelData) {
        List<Double> resistanceApparent = experimentalData.stream().map(ExperimentalData::getResistanceApparent).toList();
        List<Double> errorResistanceApparent = experimentalData.stream().map(ExperimentalData::getErrorResistanceApparent).toList();

        if (experimentalData.size() == 0 || modelData.size() == 0) {
            return new ArrayList<>();
        }

        ForwardSolver forwardSolver = ForwardSolver.getDefaultImpl();

        List<Double> solvedResistance = forwardSolver.solve(experimentalData, modelData);

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
