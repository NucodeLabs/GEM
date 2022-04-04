package ru.nucodelabs.gem.app.io;

import java.io.File;

public interface JsonFileManager {

    static JsonFileManager createDefaultFileManager() {
        return new JsonFileManagerImpl();
    }

    <T> T loadFromJson(File jsonFile, Class<T> type) throws Exception;

    <T> void saveToJson(File jsonFile, T object) throws Exception;
}
