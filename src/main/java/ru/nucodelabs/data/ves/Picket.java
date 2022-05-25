package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.DEDUCTION,
        defaultImpl = PicketImpl.class
)
public interface Picket extends Serializable {

    double DEFAULT_X_OFFSET = 100;
    double DEFAULT_Z = 0;
    String DEFAULT_NAME = "Пикет";

    static Picket createDefaultWithNewId() {
        return createWithNewId(DEFAULT_NAME, Collections.emptyList(), Collections.emptyList(), DEFAULT_X_OFFSET, DEFAULT_Z);
    }

    static Picket create(
            UUID id,
            String name,
            List<ExperimentalData> experimentalData,
            List<ModelLayer> modelData,
            double offsetX,
            double z
    ) {
        return new PicketImpl(id, name, List.copyOf(experimentalData), List.copyOf(modelData), offsetX, z);
    }

    static Picket createWithNewId(
            String name,
            List<ExperimentalData> experimentalData,
            List<ModelLayer> modelData,
            double offsetX,
            double z
    ) {
        return create(UUID.randomUUID(), name, experimentalData, modelData, offsetX, z);
    }

    @NotNull UUID getId();

    /**
     * Наименование пикета
     */
    @NotNull String getName();

    /**
     * Полевые данные
     */
    @NotNull List<@Valid @NotNull ExperimentalData> getExperimentalData();

    /**
     * Модельные данные
     */
    @NotNull @Size(max = 40) List<@Valid @NotNull ModelLayer> getModelData();

    /**
     * Смещение относительно пикета слева, или начала разреза в случае первого пикета
     */
    @Min(0) double getOffsetX();

    /**
     * Глубина
     */
    double getZ();

    default Picket withName(String name) {
        if (name.equals(getName())) {
            return this;
        } else {
            return create(getId(), name, getExperimentalData(), getModelData(), getOffsetX(), getZ());
        }
    }

    default Picket withExperimentalData(List<ExperimentalData> experimentalData) {
        if (experimentalData.equals(getExperimentalData())) {
            return this;
        } else {
            return create(getId(), getName(), experimentalData, getModelData(), getOffsetX(), getZ());
        }
    }

    default Picket withModelData(List<ModelLayer> modelData) {
        if (modelData.equals(getModelData())) {
            return this;
        } else {
            return create(getId(), getName(), getExperimentalData(), modelData, getOffsetX(), getZ());
        }
    }

    default Picket withOffsetX(double offsetX) {
        if (offsetX == getOffsetX()) {
            return this;
        } else {
            return create(getId(), getName(), getExperimentalData(), getModelData(), offsetX, getZ());
        }
    }

    default Picket withZ(double z) {
        if (z == getZ()) {
            return this;
        } else {
            return create(getId(), getName(), getExperimentalData(), getModelData(), getOffsetX(), z);
        }
    }

    default List<Double> zOfModelLayers() {
        return VesUtils.zOfPower(getModelData(), getZ());
    }
}
