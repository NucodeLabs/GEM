package ru.nucodelabs;

import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.MODFile;
import ru.nucodelabs.files.sonet.STTFile;
import ru.nucodelabs.files.sonet.SonetImport;

import java.io.File;
import java.io.FileNotFoundException;

public class ShiraPicket {
    private ShiraPicket() {

    }

    /**
     * Делает готовый Shira пикет, с готовой моделью
     * @return Пикет Shira
     * @throws FileNotFoundException Файл не найден
     */
    public static Picket getPicket() throws FileNotFoundException {
        File file_stt = new File("data/SHIRA.STT");
        STTFile sttFile = SonetImport.readSTT(file_stt);

        File file_exp = new File("data/SHIRA.EXP");
        EXPFile expFile = SonetImport.readEXP(file_exp);

        ExperimentalData experimentalData = ExperimentalData.of(sttFile, expFile);

        File file = new File("data/SHIRA_M4.mod");
        MODFile modFile = SonetImport.readMOD(file);

        ModelData modelData = ModelData.of(modFile);

        return new Picket("testPicket", experimentalData, modelData);
    }
}
