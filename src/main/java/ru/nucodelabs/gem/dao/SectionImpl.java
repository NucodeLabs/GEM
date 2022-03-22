package ru.nucodelabs.gem.dao;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.files.gem.GemJson;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.MODFile;
import ru.nucodelabs.files.sonet.STTFile;
import ru.nucodelabs.files.sonet.SonetImport;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SectionImpl implements Section {

    private final List<Picket> pickets;

    @AssistedInject
    public SectionImpl(@Assisted List<Picket> pickets) {
        this.pickets = pickets;
    }

    public SectionImpl() {
        this(new ArrayList<>());
    }

    @Override
    public ModelData getModelData(int picketNumber) {
        return pickets.get(picketNumber).modelData();
    }

    @Override
    public Picket setModelData(int picketNumber, ModelData modelData) {
        Picket oldP = pickets.get(picketNumber);
        Picket newP = new Picket(oldP.name(), oldP.experimentalData(), modelData);
        pickets.set(picketNumber, newP);
        return newP;
    }

    @Override
    public ExperimentalData getExperimentalData(int picketNumber) {
        return pickets.get(picketNumber).experimentalData();
    }

    @Override
    public Picket setExperimentalData(int picketNumber, ExperimentalData experimentalData) {
        Picket oldP = pickets.get(picketNumber);
        Picket newP = new Picket(oldP.name(), experimentalData, oldP.modelData());
        pickets.set(picketNumber, newP);
        return newP;
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
    public Picket setName(int picketNumber, String name) {
        Picket oldP = pickets.get(picketNumber);
        Picket newP = new Picket(name, oldP.experimentalData(), oldP.modelData());
        pickets.set(picketNumber, newP);
        return newP;
    }

    @Override
    public String getName(int picketNumber) {
        return pickets.get(picketNumber).name();
    }

    @Override
    public Picket loadExperimentalDataFromEXPFile(int picketNumber, File file) throws Exception {
        EXPFile expFile = SonetImport.readEXP(file);
        Path expFilePath = file.toPath();
        STTFile sttFile = sttFileOf(expFilePath, expFile);

        Picket oldPicket = pickets.get(picketNumber);
        Picket expPicket = Picket.of(sttFile, expFile);
        Picket newPicket = new Picket(expPicket.name(), expPicket.experimentalData(), oldPicket.modelData());
        pickets.set(picketNumber, newPicket);
        return newPicket;
    }

    @Override
    public Picket loadExperimentalDataFromEXPFile(File file) throws Exception {
        EXPFile expFile = SonetImport.readEXP(file);
        Path expFilePath = file.toPath();
        STTFile sttFile = sttFileOf(expFilePath, expFile);

        Picket expPicket = Picket.of(sttFile, expFile);
        pickets.add(expPicket);
        return expPicket;
    }

    private STTFile sttFileOf(Path expFilePath, EXPFile expFile) throws FileNotFoundException {
        return SonetImport.readSTT(new File(
                expFilePath.getParent().toString()
                        + File.separator
                        + expFile.getSTTFileName()));
    }

    @Override
    public Picket loadModelDataFromMODFile(int picketNumber, File file) throws Exception {
        MODFile modFile = SonetImport.readMOD(file);
        ModelData modelData = ModelData.of(modFile);
        return setModelData(picketNumber, modelData);
    }

    @Override
    public void setPicket(int picketNumber, Picket picket) {
        pickets.set(picketNumber, picket);
    }

    @Override
    public void loadFromJson(File file) throws Exception {
        List<Picket> newPickets = new GemJson().readPicketList(file);
        pickets.clear();
        pickets.addAll(newPickets);
    }

    @Override
    public void saveToJson(File file) throws Exception {
        new GemJson().writeData(pickets, file);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Section) {
            return getPickets().equals(((Section) obj).getPickets());
        } else {
            return false;
        }
    }

    @Override
    public Section clone() {
        try {
            return (Section) super.clone();
        } catch (CloneNotSupportedException e) {
            return new SectionImpl(new ArrayList<>(getPickets()));
        }
    }
}
