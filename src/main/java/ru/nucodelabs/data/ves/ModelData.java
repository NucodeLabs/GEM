package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.nucodelabs.files.sonet.MODFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private List<Integer> sizesList() {
        List<Integer> sizes = new ArrayList<>();
        if (resistance != null && resistance.size() > 0) sizes.add(resistance.size());
        if (polarization != null && polarization.size() > 0) sizes.add(polarization.size());
        if (power != null && power.size() > 0) sizes.add(power.size());

        return sizes;
    }

    @JsonIgnore
    public int getSize() {
        if (sizesList().size() > 0) {
            return Collections.min(sizesList());
        } else {
            return 0;
        }
    }
}
