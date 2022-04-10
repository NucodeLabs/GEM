package ru.nucodelabs.algorithms.forward_solver;

import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;

import java.util.ArrayList;
import java.util.List;

final class SonetForwardSolver implements ForwardSolver {

    static {
        System.loadLibrary("forwardsolver");
    }

    private final ExperimentalData experimentalData;
    private final ModelData modelData;

    SonetForwardSolver(ExperimentalData experimentalData, ModelData modelData) {
        this.experimentalData = experimentalData;
        this.modelData = modelData;
    }

    private static native double[] ves(
            double[] resistance,
            double[] power,
            int layersCnt,
            double[] AB_2,
            int distCnt);

    @Override
    public List<Double> solve() {
        List<Double> res = new ArrayList<>();
        double[] doubleArr = ves(
                modelData.resistance().stream().mapToDouble(d -> d).toArray(),
                modelData.power().stream().mapToDouble(d -> d).toArray(),
                modelData.power().size(),
                experimentalData.ab_2().stream().mapToDouble(d -> d).toArray(),
                experimentalData.ab_2().size());
        for (double number : doubleArr) {
            res.add(number);
        }
        return res;
    }
}
