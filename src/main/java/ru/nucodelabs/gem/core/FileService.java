package ru.nucodelabs.gem.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileService {
    private final Map<Object, File> dataFileMap;

    public FileService() {
        dataFileMap = new HashMap<>();
    }

    public void addAssociation(Object data, File file) {
        dataFileMap.put(data, file);
    }

    public File getAssociatedFile(Object data) {
        return dataFileMap.get(data);
    }
}
