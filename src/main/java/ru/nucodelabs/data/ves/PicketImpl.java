package ru.nucodelabs.data.ves;

import java.io.Serializable;
import java.util.List;

record PicketImpl(
        String name,
        List<ExperimentalData> experimentalData,
        List<ModelLayer> modelData
) implements Picket, Serializable {
    @Override
    public String getName() {
        return name();
    }

    @Override
    public List<ExperimentalData> getExperimentalData() {
        return experimentalData();
    }

    @Override
    public List<ModelLayer> getModelData() {
        return modelData();
    }
}
