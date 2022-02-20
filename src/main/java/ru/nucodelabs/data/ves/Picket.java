package ru.nucodelabs.data.ves;

import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.MODFile;
import ru.nucodelabs.files.sonet.STTFile;

public class Picket {
    /**
     * Наименование пикета
     */
    private String name;
    /**
     * Экспериментальные(полевые) данные
     */
    private ExperimentalData experimentalData; // get set
    /**
     * Данные модели
     */
    private ModelData modelData; // get set

    public Picket(EXPFile expFile, STTFile sttFile) {
        String fileName = expFile.getFile().getName();
        if (fileName.endsWith(".EXP") || fileName.endsWith(".exp")) {
            this.name = fileName.substring(0, fileName.length() - 4);
        } else {
            name = fileName;
        }
        experimentalData = new ExperimentalData(expFile, sttFile);
    }

    public Picket(EXPFile expFile, STTFile sttFile, MODFile modFile) {
        this(expFile, sttFile);
        modelData = new ModelData(modFile);
    }

    //region getters and setters
    public ExperimentalData getExperimentalData() {
        return experimentalData;
    }

    public void setExperimentalData(ExperimentalData experimentalData) {
        this.experimentalData = experimentalData;
    }

    public ModelData getModelData() {
        return modelData;
    }

    public void setModelData(ModelData modelData) {
        this.modelData = modelData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //endregion
}
