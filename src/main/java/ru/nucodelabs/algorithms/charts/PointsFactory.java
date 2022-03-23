package ru.nucodelabs.algorithms.charts;

import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;

import java.util.List;

public interface PointsFactory {

    static PointsFactory experimentalCurvePointsFactory(ExperimentalData experimentalData) {
        return new ExperimentalCurvePointsFactory(experimentalData);
    }

    static PointsFactory experimentalCurveUpperBoundErrorPointsFactory(ExperimentalData experimentalData) {
        return new ExperimentalCurvePointsFactory.UpperBoundErrorPointsFactory(experimentalData);
    }

    static PointsFactory experimentalCurveLowerBoundErrorPointsFactory(ExperimentalData experimentalData) {
        return new ExperimentalCurvePointsFactory.LowerBoundErrorPointsFactory(experimentalData);
    }

    static PointsFactory theoreticalCurvePointsFactory(ExperimentalData experimentalData, ModelData modelData) {
        return new TheoreticalCurvePointsFactory(experimentalData, modelData);
    }

    static PointsFactory modelCurvePointsFactory(ModelData modelData) {
        return new ModelCurvePointsFactory(modelData);
    }

    /**
     * Возвращает новые точки для графика
     *
     * @return новые точки
     */
    List<Point> points();

    /**
     * Возвращает новые точки для графика с прологарифмированными с основанием 10 значениями
     *
     * @return новые точки
     */
    List<Point> log10Points();
}
