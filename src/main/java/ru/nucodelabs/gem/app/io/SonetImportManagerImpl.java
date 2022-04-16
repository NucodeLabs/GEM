package ru.nucodelabs.gem.app.io;

import ru.nucodelabs.data.ves.ExperimentalMeasurement;
import ru.nucodelabs.data.ves.ModelLayer;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.files.sonet.*;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

class SonetImportManagerImpl implements SonetImportManager {

    @Override
    public Picket loadNameAndExperimentalDataFromEXPFile(File expFile, Picket target) throws Exception {
        String picketName;
        String fileName = expFile.getName();
        if (fileName.endsWith(".EXP") || fileName.endsWith(".exp")) {
            picketName = fileName.substring(0, fileName.length() - 4);
        } else {
            picketName = fileName;
        }

        return Picket.create(
                picketName,
                loadExperimentalDataFromEXPFile(expFile, target).getExperimentalData(),
                target.getModelData()
        );
    }

    private STTFile getSTTFile(File expFile) throws Exception {
        EXPFile expFile1 = new EXPFileParser(expFile).parse();

        Path expFilePath = expFile.toPath();

        return new STTFileParser(new File(
                expFilePath.getParent().toString()
                        + File.separator
                        + expFile1.getSTTFileName()))
                .parse();
    }

    @Override
    public Picket loadExperimentalDataFromEXPFile(File expFile, Picket target) throws Exception {
        EXPFile expFile1 = new EXPFileParser(expFile).parse();
        STTFile sttFile = getSTTFile(expFile);

        List<Integer> sizes =
                Stream.of(
                        sttFile.getAB_2(),
                        sttFile.getMN_2(),
                        expFile1.getAmperage(),
                        expFile1.getVoltage(),
                        expFile1.getResistanceApparent(),
                        expFile1.getErrorResistanceApparent()
                ).map(List::size).toList();

        int minSize = Collections.min(sizes);

        List<ExperimentalMeasurement> expData = new ArrayList<>();
        for (int i = 0; i < minSize; i++) {
            expData.add(ExperimentalMeasurement.create(
                    sttFile.getAB_2().get(i),
                    sttFile.getMN_2().get(i),
                    expFile1.getResistanceApparent().get(i),
                    expFile1.getErrorResistanceApparent().get(i),
                    expFile1.getAmperage().get(i),
                    expFile1.getVoltage().get(i)
            ));
        }

        return Picket.create(target.getName(), expData, target.getModelData());
    }

    @Override
    public Picket loadModelDataFromMODFile(File modFile, Picket target) throws Exception {
        MODFile modFile1 = new MODFileParser(modFile).parse();
        List<Integer> sizes = Stream.of(
                modFile1.getPower(),
                modFile1.getResistance()
        ).map(List::size).toList();

        int minSize = Collections.min(sizes);

        List<ModelLayer> modelData = new ArrayList<>();
        for (int i = 0; i < minSize; i++) {
            modelData.add(ModelLayer.create(
                    modFile1.getPower().get(i),
                    modFile1.getResistance().get(i)));
        }

        return Picket.create(target.getName(), target.getExperimentalData(), modelData);
    }
}
