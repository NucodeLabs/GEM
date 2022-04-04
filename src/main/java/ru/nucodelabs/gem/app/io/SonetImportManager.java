package ru.nucodelabs.gem.app.io;

import ru.nucodelabs.data.ves.Picket;

import java.io.File;

public interface SonetImportManager {

    static SonetImportManager create() {
        return new SonetImportManagerImpl();
    }

    /**
     * Загружает имя и экспериментальные данные в пикет
     */
    Picket loadNameAndExperimentalDataFromEXPFile(File expFile) throws Exception;

    /**
     * Загружает только экспериментальные данные в пикет
     */
    Picket loadExperimentalDataFromEXPFile(File expFile, Picket picket) throws Exception;

    /**
     * Загружает модельные данные в пикет
     */
    Picket loadModelDataFromMODFile(File modFile, Picket picket) throws Exception;
}
