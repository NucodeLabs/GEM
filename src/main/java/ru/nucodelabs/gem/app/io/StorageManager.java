package ru.nucodelabs.gem.app.io;

import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Загружает файлы и хранит копию сохраненного на диске состояния разреза
 */
public class StorageManager implements JsonFileManager, SonetImportManager {

    private final JsonFileManager jsonFileManagerDelegate;
    private final SonetImportManager sonetImportManagerDelegate;
    private Section savedState;
    private File savedStateFile;

    @Inject
    public StorageManager(JsonFileManager jsonFileManagerDelegate, SonetImportManager sonetImportManagerDelegate) {
        this.jsonFileManagerDelegate = jsonFileManagerDelegate;
        this.sonetImportManagerDelegate = sonetImportManagerDelegate;
        savedState = Section.create(Collections.emptyList());
    }

    @Override
    public Picket loadNameAndExperimentalDataFromEXPFile(File expFile, Picket target) throws Exception {
        return sonetImportManagerDelegate.loadNameAndExperimentalDataFromEXPFile(expFile, target);
    }

    @Override
    public Picket loadExperimentalDataFromEXPFile(File expFile, Picket picket) throws Exception {
        return sonetImportManagerDelegate.loadExperimentalDataFromEXPFile(expFile, picket);
    }

    @Override
    public Picket loadModelDataFromMODFile(File modFile, Picket picket) throws Exception {
        return sonetImportManagerDelegate.loadModelDataFromMODFile(modFile, picket);
    }

    public Section getSavedState() {
        return Section.create(List.copyOf(savedState.getPickets()));
    }

    public boolean compareWithSavedState(Section toCompare) {
        return getSavedState().equals(toCompare);
    }

    public void clearSavedState() {
        savedState = Section.create(Collections.emptyList());
    }

    @Nullable
    public File getSavedStateFile() {
        return savedStateFile;
    }

    @Override
    public <T extends Serializable> T loadFromJson(File jsonFile, Class<T> type) throws Exception {
        T loaded = jsonFileManagerDelegate.loadFromJson(jsonFile, type);
        if (loaded instanceof Section) {
            savedState = Section.create(List.copyOf(((Section) loaded).getPickets()));
            savedStateFile = jsonFile;
        }
        return loaded;
    }

    @Override
    public <T extends Serializable> void saveToJson(File jsonFile, T object) throws Exception {
        jsonFileManagerDelegate.saveToJson(jsonFile, object);
        if (object instanceof Section) {
            savedState = Section.create(List.copyOf(((Section) object).getPickets()));
            savedStateFile = jsonFile;
        }
    }
}
