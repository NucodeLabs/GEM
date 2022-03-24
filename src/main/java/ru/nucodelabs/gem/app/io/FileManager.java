package ru.nucodelabs.gem.app.io;

import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.files.sonet.*;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public interface FileManager {

    static FileManager createDefaultFileManager() {
        return new FileManagerImpl();
    }

    List<Picket> loadSectionFromJsonFile(File jsonFile) throws Exception;

    void saveSectionToJsonFile(File jsonFile, List<Picket> section) throws Exception;

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
