package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import ru.nucodelabs.files.sonet.MODFile;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNullElse;

public record ModelData(
        // Сопротивление, Ом * м
        @NotNull List<@Min(0) Double> resistance,
        // Поляризация, %
        @NotNull List<@Min(0) Double> polarization,
        // Мощность, м
        @NotNull List<@Min(0) Double> power
) implements Sizeable {
    public static ModelData from(MODFile modFile) {
        return new ModelData(
                modFile.getResistance(),
                modFile.getPolarization(),
                modFile.getPower()
        );
    }

    @JsonIgnore
    public List<ModelDataRow> getRows() {
        List<ModelDataRow> res = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            res.add(
                    new ModelDataRow(
                            i,
                            requireNonNullElse(resistance().get(i), 0d),
                            requireNonNullElse(power().get(i), 0d),
                            requireNonNullElse(polarization().get(i), 0d)
                    )
            );
        }
        return res;
    }

    @Override
    public ModelData clone() {
        try {
            return (ModelData) super.clone();
        } catch (CloneNotSupportedException e) {
            return new ModelData(
                    new ArrayList<>(resistance()),
                    new ArrayList<>(polarization()),
                    new ArrayList<>(power())
            );
        }
    }
}
