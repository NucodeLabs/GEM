package ru.nucodelabs.algorithms.charts;

import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;

import java.util.List;

public class VESChartsService {

    public List<Point> experimentalCurveOf(ExperimentalData experimentalData) {
        return new ExperimentalCurvePointsFactory(experimentalData).points();
    }

    public List<Point> experimentalCurveErrorUpperBoundOf(ExperimentalData experimentalData) {
        return new ExperimentalCurvePointsFactory.UpperBoundErrorPointsFactory(experimentalData).points();
    }

    public List<Point> experimentalCurveErrorLowerBoundOf(ExperimentalData experimentalData) {
        return new ExperimentalCurvePointsFactory.LowerBoundErrorPointsFactory(experimentalData).points();
    }

    public List<Point> theoreticalCurveOf(ExperimentalData experimentalData, ModelData modelData) {
        return new TheoreticalCurvePointsFactory(experimentalData, modelData).points();
    }

    public List<Point> modelCurveOf(ModelData modelData) {
        return new ModelCurvePointsFactory(modelData).points();
    }
}
