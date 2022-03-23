package ru.nucodelabs;

import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.files.sonet.*;

import java.io.File;
import java.util.Objects;

public class ShiraPicket {
    private ShiraPicket() {

    }

    /**
     * Делает готовый Shira пикет, с готовой моделью
     *
     * @return Пикет Shira
     */
    public static Picket getPicket() {
        File file_stt = new File("data/SHIRA.STT");
        STTFile sttFile = null;
        try {
            sttFile = new STTFileParser(file_stt).parse();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File file_exp = new File("data/SHIRA.EXP");
        EXPFile expFile = null;
        try {
            expFile = new EXPFileParser(file_exp).parse();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ExperimentalData experimentalData =
                ExperimentalData.from(Objects.requireNonNull(sttFile), Objects.requireNonNull(expFile));

        File file_mod = new File("data/SHIRA_M2.mod");
        MODFile modFile = null;
        try {
            modFile = new MODFileParser(file_mod).parse();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ModelData modelData =
                ModelData.from(Objects.requireNonNull(modFile));

        return new Picket("testPicket", experimentalData, modelData);
    }
}
