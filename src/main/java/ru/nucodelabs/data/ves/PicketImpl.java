package ru.nucodelabs.data.ves;

import java.io.Serializable;
import java.util.List;

record PicketImpl(
        String name,
        List<ExperimentalMeasurement> experimentalData,
        List<ModelLayer> modelData
) implements Picket, Serializable {
    @Override
    public String getName() {
        return name();
    }

    @Override
    public List<ExperimentalMeasurement> getExperimentalData() {
        return experimentalData();
    }

    @Override
    public List<ModelLayer> getModelData() {
        return modelData();
    }
}
