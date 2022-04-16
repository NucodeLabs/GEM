package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.DEDUCTION,
        defaultImpl = PicketImpl.class
)
public interface Picket {
    static Picket create(
            String name,
            List<ExperimentalMeasurement> experimentalData,
            List<ModelLayer> modelData
    ) {
        return new PicketImpl(name, experimentalData, modelData);
    }

    /**
     * Наименование пикета
     */
    @NotNull String getName();

    /**
     * Полевые данные
     */
    @NotNull @Valid List<ExperimentalMeasurement> getExperimentalData();

    /**
     * Модельные данные
     */
    @NotNull @Valid List<ModelLayer> getModelData();
}
