package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.DEDUCTION,
        defaultImpl = PicketImpl.class
)
public interface Picket extends Serializable {

    double DEFAULT_X = 0;
    double DEFAULT_Z = 0;

    Picket EMPTY = Picket.create("", Collections.emptyList(), Collections.emptyList());

    static Class<? extends Picket> implClass() {
        return PicketImpl.class;
    }

    static Picket create(
            String name,
            List<ExperimentalData> experimentalData,
            List<ModelLayer> modelData
    ) {
        return new PicketImpl(name, experimentalData, modelData, Picket.DEFAULT_X, Picket.DEFAULT_Z);
    }

    static Picket create(
            String name,
            List<ExperimentalData> experimentalData,
            List<ModelLayer> modelData,
            double x,
            double z
    ) {
        return new PicketImpl(name, experimentalData, modelData, x, z);
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

    /**
     * Относительное смещение от начала разреза
     */
    @Min(0) double getX();

    /**
     * Глубина
     */
    double getZ();
}
