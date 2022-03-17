package ru.nucodelabs.data.ves;

import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.STTFile;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public record Picket(
        // Наименование пикета
        @NotNull String name,
        // Экспериментальные(полевые) данные
        @Valid @Nullable ExperimentalData experimentalData,
        // Данные модели
        @Valid @Nullable ModelData modelData
) {
    public static Picket of(STTFile sttFile, EXPFile expFile) {
        String fileName = expFile.getFile().getName();
        String newName;
        if (fileName.endsWith(".EXP") || fileName.endsWith(".exp")) {
            newName = fileName.substring(0, fileName.length() - 4);
        } else {
            newName = fileName;
        }
        ExperimentalData newExperimentalData = ExperimentalData.of(sttFile, expFile);
        return new Picket(newName, newExperimentalData, null);
    }
}
