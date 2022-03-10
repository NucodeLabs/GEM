package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.nucodelabs.files.sonet.MODFile;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNullElse;
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

    @JsonIgnore
    public List<ModelTableLine> getLines() {
        List<ModelTableLine> res = new ArrayList<>();
        for (int i = 0; i < getSize(); i++) {
            res.add(
                    new ModelTableLine(
                            i + 1,
                            requireNonNullElse(resistance().get(i), 0d),
                            requireNonNullElse(power().get(i), 0d),
                            requireNonNullElse(polarization().get(i), 0d)
                    )
            );
        }
        return res;
    }
}
