package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

record PicketImpl(
        UUID id,
        String name,
        List<ExperimentalData> experimentalData,
        List<ModelLayer> modelData,
        double offsetX,
        double z
) implements Picket, Serializable {

    @JsonCreator
    static PicketImpl autoID(
            @JsonProperty("name") String name,
            @JsonProperty("experimentalData") List<ExperimentalData> experimentalData,
            @JsonProperty("modelData") List<ModelLayer> modelData,
            @JsonProperty("offsetX") double offsetX,
            @JsonProperty("z") double z
    ) {
        return new PicketImpl(UUID.randomUUID(), name, experimentalData, modelData, offsetX, z);
    }

    @Override
    @JsonIgnore
    public UUID getId() {
        return id();
    }

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

    @Override
    public double getOffsetX() {
        return offsetX();
    }

    @Override
    public double getZ() {
        return z();
    }
}
