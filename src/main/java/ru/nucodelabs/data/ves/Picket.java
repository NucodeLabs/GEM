package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.DEDUCTION,
        defaultImpl = PicketImpl.class
)
public interface Picket extends Serializable {

    double DEFAULT_X_OFFSET = 100;
    double DEFAULT_Z = 0;

    Picket EMPTY = Picket.create("", Collections.emptyList(), Collections.emptyList());

    Class<? extends Picket> IMPL_CLASS = PicketImpl.class;

    static Picket create(
            String name,
            List<ExperimentalData> experimentalData,
            List<ModelLayer> modelData
    ) {
        return new PicketImpl(name, experimentalData, modelData, Picket.DEFAULT_X_OFFSET, Picket.DEFAULT_Z);
    }

    static Picket create(
            String name,
            List<ExperimentalData> experimentalData,
            List<ModelLayer> modelData,
            double xOffset,
            double z
    ) {
        return new PicketImpl(name, experimentalData, modelData, xOffset, z);
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
     * Смещение относительно пикета слева, или начала разреза в случае первого пикета
     */
    @Positive double getOffsetX();

    /**
     * Глубина
     */
    double getZ();
}
