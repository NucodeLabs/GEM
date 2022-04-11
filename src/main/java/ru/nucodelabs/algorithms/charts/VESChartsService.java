package ru.nucodelabs.algorithms.charts;

import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;

import java.util.List;

public class VESChartsService {

    private final boolean log10;

    public VESChartsService(boolean log10) {
        this.log10 = log10;
    }

    public List<Point> experimentalCurveOf(ExperimentalData experimentalData) {
        return log10 ? new ExperimentalCurvePointsFactory(experimentalData).log10Points()
                : new ExperimentalCurvePointsFactory(experimentalData).points();
    }

    public List<Point> experimentalCurveErrorUpperBoundOf(ExperimentalData experimentalData) {
        return log10 ? new ExperimentalCurvePointsFactory.UpperBoundErrorPointsFactory(experimentalData).log10Points()
                : new ExperimentalCurvePointsFactory.UpperBoundErrorPointsFactory(experimentalData).points();
    }

    public List<Point> experimentalCurveErrorLowerBoundOf(ExperimentalData experimentalData) {
        return log10 ? new ExperimentalCurvePointsFactory.LowerBoundErrorPointsFactory(experimentalData).log10Points()
                : new ExperimentalCurvePointsFactory.LowerBoundErrorPointsFactory(experimentalData).points();
    }

    public List<Point> theoreticalCurveOf(ExperimentalData experimentalData, ModelData modelData) {
        return log10 ? new TheoreticalCurvePointsFactory(experimentalData, modelData).log10Points()
                : new TheoreticalCurvePointsFactory(experimentalData, modelData).points();
    }

    public List<Point> modelCurveOf(ModelData modelData) {
        return log10 ? new ModelCurvePointsFactory(modelData).log10Points()
                : new ModelCurvePointsFactory(modelData).points();
    }
}
