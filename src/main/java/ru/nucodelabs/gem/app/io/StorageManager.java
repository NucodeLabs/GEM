package ru.nucodelabs.gem.app.io;

import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.snapshot.Snapshot;

import javax.inject.Inject;
import java.io.File;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Загружает файлы и хранит копию сохраненного на диске состояния разреза
 */
public class StorageManager implements JsonFileManager, SonetImportManager {

    private final JsonFileManager jsonFileManagerDelegate;
    private final SonetImportManager sonetImportManagerDelegate;
    private Snapshot<Section> savedState = Snapshot.create(Section.DEFAULT);
    private File savedStateFile = null;

    @Inject
    public StorageManager(JsonFileManager jsonFileManagerDelegate, SonetImportManager sonetImportManagerDelegate) {
        this.jsonFileManagerDelegate = jsonFileManagerDelegate;
        this.sonetImportManagerDelegate = sonetImportManagerDelegate;
    }

    @Override
    public Picket loadNameAndExperimentalDataFromEXPFile(File expFile, Picket target) throws Exception {
        return sonetImportManagerDelegate.loadNameAndExperimentalDataFromEXPFile(expFile, target);
    }

    @Override
    public Picket loadExperimentalDataFromEXPFile(File expFile, Picket target) throws Exception {
        return sonetImportManagerDelegate.loadExperimentalDataFromEXPFile(expFile, target);
    }

    @Override
    public Picket loadModelDataFromMODFile(File modFile, Picket target) throws Exception {
        return sonetImportManagerDelegate.loadModelDataFromMODFile(modFile, target);
    }

    public Snapshot<Section> getSavedState() {
        return savedState;
    }

    public boolean compareWithSavedState(Snapshot<Section> toCompare) {
        return Objects.equals(toCompare, getSavedState());
    }

    public void clearSavedState() {
        savedState = Snapshot.create(Section.DEFAULT);
        savedStateFile = null;
    }

    public Optional<File> getSavedStateFile() {
        return Optional.ofNullable(savedStateFile);
    }

    @Override
    public <T extends Serializable> T loadFromJson(File jsonFile, Class<T> type) throws Exception {
        T loaded = jsonFileManagerDelegate.loadFromJson(jsonFile, type);
        if (loaded instanceof Section section) {
            savedState = Snapshot.create(section);
            savedStateFile = jsonFile;
        }
        return loaded;
    }

    @Override
    public <T extends Serializable> void saveToJson(File jsonFile, T object) throws Exception {
        jsonFileManagerDelegate.saveToJson(jsonFile, object);
        if (object instanceof Section section) {
            savedState = Snapshot.create(section);
            savedStateFile = jsonFile;
        }
    }
}
