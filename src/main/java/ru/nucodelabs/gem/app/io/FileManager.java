package ru.nucodelabs.gem.app.io;

import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.files.sonet.*;

import java.io.File;
import java.nio.file.Path;

public interface FileManager {

    static FileManager createDefaultFileManager() {
        return new FileManagerImpl();
    }

    Section loadSectionFromJsonFile(File jsonFile) throws Exception;

    void saveSectionToJsonFile(File jsonFile, Section section) throws Exception;

    Picket loadPicketFromJsonFile(File jsonFile) throws Exception;

    void savePicketToJsonFile(File jsonFile, Picket picket) throws Exception;

    default Picket loadPicketFromEXPFile(File expFile) throws Exception {
        EXPFile expFile1 = new EXPFileParser(expFile).parse();
        Path expFilePath = expFile.toPath();
        STTFile sttFile = new STTFileParser(new File(
                expFilePath.getParent().toString()
                        + File.separator
                        + expFile1.getSTTFileName()))
                .parse();

        return Picket.from(sttFile, expFile1);
    }

    default Picket loadExperimentalDataFromEXPFile(File expFile, Picket picket) throws Exception {
        Picket loadedPicket = loadPicketFromEXPFile(expFile);

        return new Picket(picket.name(), loadedPicket.experimentalData(), picket.modelData());
    }

    default Picket loadModelDataFromMODFile(File modFile, Picket picket) throws Exception {
        MODFile modFile1 = new MODFileParser(modFile).parse();
        ModelData loadedModelData = ModelData.from(modFile1);

        return new Picket(picket.name(), picket.experimentalData(), loadedModelData);
    }
}
