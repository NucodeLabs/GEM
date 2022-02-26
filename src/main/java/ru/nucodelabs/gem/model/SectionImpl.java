package ru.nucodelabs.gem.model;

import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.files.gem.GemJson;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.MODFile;
import ru.nucodelabs.files.sonet.STTFile;
import ru.nucodelabs.files.sonet.SonetImport;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SectionImpl implements Section {

    private List<Picket> pickets;

    public SectionImpl() {
        pickets = new ArrayList<>();
    }

    @Override
    public ModelData getModelData(int picketNumber) {
        return pickets.get(picketNumber).getModelData();
    }

    @Override
    public void setModelData(int picketNumber, ModelData modelData) {
        pickets.get(picketNumber).setModelData(modelData);
    }

    @Override
    public ExperimentalData getExperimentalData(int picketNumber) {
        return pickets.get(picketNumber).getExperimentalData();
    }

    @Override
    public void setExperimentalData(int picketNumber, ExperimentalData experimentalData) {
        pickets.get(picketNumber).setExperimentalData(experimentalData);
    }

    @Override
    public List<Picket> getPickets() {
        return pickets;
    }

    @Override
    public Picket getPicket(int picketNumber) {
        return pickets.get(picketNumber);
    }

    @Override
    public void addPicket(Picket picket) {
        pickets.add(picket);
    }

    @Override
    public void removePicket(int picketNumber) {
        pickets.remove(picketNumber);
    }

    @Override
    public int getPicketsCount() {
        return pickets.size();
    }

    @Override
    public Picket getLastPicket() {
        return pickets.get(pickets.size() - 1);
    }

    @Override
    public void setName(int picketNumber, String name) {
        pickets.get(picketNumber).setName(name);
    }

    @Override
    public String getName(int picketNumber) {
        return pickets.get(picketNumber).getName();
    }

    @Override
    public void loadExperimentalDataFromEXPFile(int picketNumber, File file) throws Exception {
        EXPFile expFile = SonetImport.readEXP(file);
        Path expFilePath = file.toPath();
        STTFile sttFile = SonetImport.readSTT(new File(
                expFilePath.getParent().toString()
                        + File.separator
                        + expFile.getSTTFileName()));

        Picket newPicket = new Picket(expFile, sttFile);
        if (pickets.size() > picketNumber + 1) {
            pickets.set(picketNumber, newPicket);
        } else {
            addPicket(newPicket);
        }
    }

    @Override
    public void loadModelDataFromMODFile(int picketNumber, File file) throws Exception {
        MODFile modFile = SonetImport.readMOD(file);
        ModelData modelData = new ModelData(modFile);
        setModelData(picketNumber, modelData);
    }

    @Override
    public void loadFromJson(File file) throws Exception {
        this.pickets = GemJson.readPicketList(file);
    }

    @Override
    public void saveToJson(File file) throws Exception {
        GemJson.writeData(pickets, file);
    }
}
