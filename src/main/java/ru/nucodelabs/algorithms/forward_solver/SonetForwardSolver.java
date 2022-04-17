package ru.nucodelabs.algorithms.forward_solver;

import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelLayer;

import java.util.ArrayList;
import java.util.List;

final class SonetForwardSolver implements ForwardSolver {

    static {
        System.loadLibrary("forwardsolver");
    }

    private final List<ExperimentalData> experimentalData;
    private final List<ModelLayer> modelData;

    SonetForwardSolver(List<ExperimentalData> experimentalData, List<ModelLayer> modelData) {
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
                modelData.stream().map(ModelLayer::getResistance).mapToDouble(d -> d).toArray(),
                modelData.stream().map(ModelLayer::getPower).mapToDouble(d -> d).toArray(),
                modelData.size(),
                experimentalData.stream().map(ExperimentalData::getAb2).mapToDouble(d -> d).toArray(),
                experimentalData.size());
        for (double number : doubleArr) {
            res.add(number);
        }
        return res;
    }
}
