package ru.nucodelabs.gem.app.io;

import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;

import javax.inject.Inject;
import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Загружает файлы и хранит копию сохраненного на диске состояния разреза
 */
public class StorageManager implements FileManager {

    private final FileManager fileManagerDelegate;
    private Section savedState;
    private File savedStateFile;

    @Inject
    public StorageManager(FileManager fileManagerDelegate) {
        this.fileManagerDelegate = fileManagerDelegate;
        savedState = new Section(Collections.emptyList());
    }

    @Override
    public Section loadSectionFromJsonFile(File jsonFile) throws Exception {
        Section loaded = fileManagerDelegate.loadSectionFromJsonFile(jsonFile);
        savedState = new Section(List.copyOf(loaded.pickets()));
        savedStateFile = jsonFile;
        return loaded;
    }

    @Override
    public void saveSectionToJsonFile(File jsonFile, Section section) throws Exception {
        savedState = new Section(List.copyOf(section.pickets()));
        savedStateFile = jsonFile;
        fileManagerDelegate.saveSectionToJsonFile(jsonFile, section);
    }

    @Override
    public Picket loadPicketFromJsonFile(File jsonFile) throws Exception {
        return fileManagerDelegate.loadPicketFromJsonFile(jsonFile);
    }

    @Override
    public void savePicketToJsonFile(File jsonFile, Picket picket) throws Exception {
        fileManagerDelegate.savePicketToJsonFile(jsonFile, picket);
    }

    @Override
    public Picket loadPicketFromEXPFile(File expFile) throws Exception {
        return fileManagerDelegate.loadPicketFromEXPFile(expFile);
    }

    @Override
    public Picket loadExperimentalDataFromEXPFile(File expFile, Picket picket) throws Exception {
        return fileManagerDelegate.loadExperimentalDataFromEXPFile(expFile, picket);
    }

    @Override
    public Picket loadModelDataFromMODFile(File modFile, Picket picket) throws Exception {
        return fileManagerDelegate.loadModelDataFromMODFile(modFile, picket);
    }

    public Section getSavedState() {
        return new Section(List.copyOf(savedState.pickets()));
    }

    public boolean compareWithSavedState(Section toCompare) {
        return getSavedState().equals(toCompare);
    }

    public void clearSavedState() {
        savedState = new Section(Collections.emptyList());
    }

    public File getSavedStateFile() {
        return savedStateFile;
    }
}
