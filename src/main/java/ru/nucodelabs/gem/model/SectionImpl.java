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
import java.util.Collections;
import java.util.List;

public class SectionImpl implements Section {

    private List<Picket> pickets;

    public SectionImpl() {
        pickets = new ArrayList<>();
    }

    @Override
    public ModelData getModelData(int picketNumber) {
        return pickets.get(picketNumber).modelData();
    }

    @Override
    public void setModelData(int picketNumber, ModelData modelData) {
        Picket oldP = pickets.get(picketNumber);
        Picket newP = new Picket(oldP.name(), oldP.experimentalData(), modelData);
        pickets.set(picketNumber, newP);
    }

    @Override
    public ExperimentalData getExperimentalData(int picketNumber) {
        return pickets.get(picketNumber).experimentalData();
    }

    @Override
    public void setExperimentalData(int picketNumber, ExperimentalData experimentalData) {
        Picket oldP = pickets.get(picketNumber);
        Picket newP = new Picket(oldP.name(), experimentalData, oldP.modelData());
        pickets.set(picketNumber, newP);
    }

    @Override
    public List<Picket> getPickets() {
        return Collections.unmodifiableList(pickets);
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
    public void swapPickets(int picketNumber1, int picketNumber2) {
        Collections.swap(pickets, picketNumber1, picketNumber2);
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
        Picket oldP = pickets.get(picketNumber);
        Picket newP = new Picket(name, oldP.experimentalData(), oldP.modelData());
        pickets.set(picketNumber, newP);
    }

    @Override
    public String getName(int picketNumber) {
        return pickets.get(picketNumber).name();
    }

    @Override
    public void loadExperimentalDataFromEXPFile(int picketNumber, File file) throws Exception {
        EXPFile expFile = SonetImport.readEXP(file);
        Path expFilePath = file.toPath();
        STTFile sttFile = SonetImport.readSTT(new File(
                expFilePath.getParent().toString()
                        + File.separator
                        + expFile.getSTTFileName()));

        Picket newPicket = Picket.of(sttFile, expFile);
        if (pickets.size() > picketNumber + 1) {
            pickets.set(picketNumber, newPicket);
        } else {
            addPicket(newPicket);
        }
    }

    @Override
    public void loadModelDataFromMODFile(int picketNumber, File file) throws Exception {
        MODFile modFile = SonetImport.readMOD(file);
        ModelData modelData = ModelData.of(modFile);
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
