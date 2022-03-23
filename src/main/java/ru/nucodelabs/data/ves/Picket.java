package ru.nucodelabs.data.ves;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.STTFile;

import javax.annotation.Nullable;

public record Picket(
        // Наименование пикета
        @NotNull String name,
        // Экспериментальные(полевые) данные
        @Valid @NotNull ExperimentalData experimentalData,
        // Данные модели
        @Valid @Nullable ModelData modelData
) {
    public static Picket from(STTFile sttFile, EXPFile expFile) {
        String fileName = expFile.getFile().getName();
        String newName;
        if (fileName.endsWith(".EXP") || fileName.endsWith(".exp")) {
            newName = fileName.substring(0, fileName.length() - 4);
        } else {
            newName = fileName;
        }
        ExperimentalData newExperimentalData = ExperimentalData.from(sttFile, expFile);
        return new Picket(newName, newExperimentalData, null);
    }
}
