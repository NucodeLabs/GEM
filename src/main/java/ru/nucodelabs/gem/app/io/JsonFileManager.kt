package ru.nucodelabs.gem.app.io;

import java.io.File;
import java.io.Serializable;

public interface JsonFileManager {

    static JsonFileManager createDefault() {
        return new JsonFileManagerImpl();
    }

    <T extends Serializable> T loadFromJson(File jsonFile, Class<T> type) throws Exception;

    <T extends Serializable> void saveToJson(File jsonFile, T object) throws Exception;
}
