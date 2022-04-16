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
    Picket loadNameAndExperimentalDataFromEXPFile(File expFile, Picket target) throws Exception;

    /**
     * Загружает только экспериментальные данные в пикет
     */
    Picket loadExperimentalDataFromEXPFile(File expFile, Picket target) throws Exception;

    /**
     * Загружает модельные данные в пикет
     */
    Picket loadModelDataFromMODFile(File modFile, Picket target) throws Exception;
}
