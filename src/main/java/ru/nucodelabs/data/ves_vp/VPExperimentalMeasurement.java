package ru.nucodelabs.data.ves_vp;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Min;
import ru.nucodelabs.data.ves.ExperimentalMeasurement;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.DEDUCTION,
        defaultImpl = VPExperimentalMeasurementImpl.class
)
public interface VPExperimentalMeasurement extends ExperimentalMeasurement {

    static ExperimentalMeasurement create(
            double ab2,
            double mn2,
            double resistanceApparent,
            double errorResistanceApparent,
            double amperage,
            double voltage,
            double polarizationApparent,
            double errorPolarizationApparent
    ) {
        return new VPExperimentalMeasurementImpl(
                ab2, mn2, resistanceApparent, errorResistanceApparent, amperage, voltage,
                polarizationApparent, errorPolarizationApparent);
    }

    /**
     * Поляризация кажущаяся, %
     */
    @Min(0) double getPolarizationApparent();

    /**
     * Погрешность, %
     */
    @Min(0) double getErrorPolarizationApparent();
}
