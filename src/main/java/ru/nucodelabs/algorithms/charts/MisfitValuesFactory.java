package ru.nucodelabs.algorithms.charts;

import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;

import java.util.List;
import java.util.function.BiFunction;

public interface MisfitValuesFactory extends BiFunction<ExperimentalData, ModelData, List<Double>> {

    MisfitValuesFactory DEFAULT_IMPL = new MisfitValuesFactoryNative();

    static MisfitValuesFactory getDefaultMisfitValuesFactory() {
        return DEFAULT_IMPL;
    }

    @Override
    List<Double> apply(ExperimentalData experimentalData, ModelData modelData);
}
