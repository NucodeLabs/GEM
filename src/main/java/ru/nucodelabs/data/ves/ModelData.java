package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.nucodelabs.files.sonet.MODFile;

import java.util.List;

import static ru.nucodelabs.data.ves.Sizes.minSize;

public record ModelData(
        // Сопротивление, Ом * м
        List<Double> resistance,
        // Поляризация, %
        List<Double> polarization,
        // Мощность, м
        List<Double> power
) {
    public static ModelData of(MODFile modFile) {
        return new ModelData(
                modFile.getResistance(),
                modFile.getPolarization(),
                modFile.getPower()
        );
    }

    @JsonIgnore
    public int getSize() {
        return minSize(this, true);
    }
}
