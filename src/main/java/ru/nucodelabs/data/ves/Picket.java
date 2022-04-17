package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.DEDUCTION,
        defaultImpl = PicketImpl.class
)
public interface Picket extends Serializable {
    static Picket create(
            String name,
            List<ExperimentalData> experimentalData,
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
    @NotNull @Valid List<ExperimentalData> getExperimentalData();

    /**
     * Модельные данные
     */
    @NotNull @Valid @Size(max = 40) List<ModelLayer> getModelData();
}
