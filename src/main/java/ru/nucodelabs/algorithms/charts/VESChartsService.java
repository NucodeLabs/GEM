package ru.nucodelabs.algorithms.charts;


import ru.nucodelabs.data.ves.ExperimentalMeasurement;
import ru.nucodelabs.data.ves.ModelLayer;

import java.util.List;

public class VESChartsService {

    public List<Point> experimentalCurveOf(List<ExperimentalMeasurement> experimentalData) {
        return new ExperimentalCurvePointsConverter(experimentalData).points();
    }

    public List<Point> experimentalCurveErrorUpperBoundOf(List<ExperimentalMeasurement> experimentalData) {
        return new ExperimentalCurvePointsConverter.UpperBoundErrorPointsConverter(experimentalData).points();
    }

    public List<Point> experimentalCurveErrorLowerBoundOf(List<ExperimentalMeasurement> experimentalData) {
        return new ExperimentalCurvePointsConverter.LowerBoundErrorPointsConverter(experimentalData).points();
    }

    public List<Point> theoreticalCurveOf(List<ExperimentalMeasurement> experimentalData, List<ModelLayer> modelData) {
        return new TheoreticalCurvePointsConverter(experimentalData, modelData).points();
    }

    public List<Point> modelCurveOf(List<ModelLayer> modelData) {
        return new ModelCurvePointsConverter(modelData).points();
    }
}
