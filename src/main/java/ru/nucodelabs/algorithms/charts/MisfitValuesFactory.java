package ru.nucodelabs.algorithms.charts;

import ru.nucodelabs.algorithms.forward_solver.ForwardSolver;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import static java.lang.Math.abs;
import static java.lang.Math.signum;

public class MisfitValuesFactory implements BiFunction<ExperimentalData, ModelData, List<Double>> {
    @Override
    public List<Double> apply(ExperimentalData experimentalData, ModelData modelData) {
        if (experimentalData.size() == 0 || modelData.size() == 0) {
            return new ArrayList<>();
        }

        ForwardSolver forwardSolver = ForwardSolver.createDefaultForwardSolver(experimentalData, modelData);

        List<Double> solvedResistance = forwardSolver.solve();

        List<Double> res = new ArrayList<>();

        for (int i = 0; i < experimentalData.size(); i++) {
            double value = abs(MisfitFunctions.calculateRelativeDeviationWithError(
                    experimentalData.resistanceApparent().get(i),
                    experimentalData.errorResistanceApparent().get(i) / 100f,
                    solvedResistance.get(i)
            )) * signum(solvedResistance.get(i) - experimentalData.resistanceApparent().get(i)) * 100f;
            res.add(value);
        }

        return res;
    }
}
