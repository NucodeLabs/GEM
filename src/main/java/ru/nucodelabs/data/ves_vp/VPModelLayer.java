package ru.nucodelabs.data.ves_vp;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Min;
import ru.nucodelabs.data.ves.ModelLayer;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.DEDUCTION,
        defaultImpl = VPModelLayerImpl.class
)
public interface VPModelLayer extends ModelLayer {

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
}
