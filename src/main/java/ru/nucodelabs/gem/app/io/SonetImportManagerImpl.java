package ru.nucodelabs.gem.app.io;

import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.files.sonet.*;

import java.io.File;
import java.nio.file.Path;

public class SonetImportManagerImpl implements SonetImportManager {

    @Override
    public Picket loadNameAndExperimentalDataFromEXPFile(File expFile) throws Exception {
        EXPFile expFile1 = new EXPFileParser(expFile).parse();
        Path expFilePath = expFile.toPath();
        STTFile sttFile = new STTFileParser(new File(
                expFilePath.getParent().toString()
                        + File.separator
                        + expFile1.getSTTFileName()))
                .parse();

        return Picket.from(sttFile, expFile1);
    }

    @Override
    public Picket loadExperimentalDataFromEXPFile(File expFile, Picket picket) throws Exception {
        Picket loadedPicket = loadNameAndExperimentalDataFromEXPFile(expFile);

        return new Picket(picket.name(), loadedPicket.experimentalData(), picket.modelData());
    }

    @Override
    public Picket loadModelDataFromMODFile(File modFile, Picket picket) throws Exception {
        MODFile modFile1 = new MODFileParser(modFile).parse();
        ModelData loadedModelData = ModelData.from(modFile1);

        return new Picket(picket.name(), picket.experimentalData(), loadedModelData);
    }
}
