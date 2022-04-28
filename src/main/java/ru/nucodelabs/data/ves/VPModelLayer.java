package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Min;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.DEDUCTION,
        defaultImpl = VPModelLayerImpl.class
)
public sealed interface VPModelLayer extends ModelLayer permits VPModelLayerImpl {

    static VPModelLayer create(
            double power,
            double resistance,
            double polarization
    ) {
        return new VPModelLayerImpl(power, resistance, polarization);
    }

    /**
     * Поляризация, %
     */
    @Min(0) double getPolarization();

    default VPModelLayer withPolarization(double polarization) {
        if (polarization == getPolarization()) {
            return this;
        } else {
            return create(getPower(), getPower(), polarization);
        }
    }
}
