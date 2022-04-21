package ru.nucodelabs;

import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.app.io.SonetImportManager;

import java.io.File;

public class ShiraPicket {
    private ShiraPicket() {

    }

    /**
     * Делает готовый Shira пикет, с готовой моделью
     *
     * @return Пикет Shira
     */
    public static Picket getPicket() throws Exception {
        File file_stt = new File("data/SHIRA.STT");

        File file_exp = new File("data/SHIRA.EXP");

        File file_mod = new File("data/SHIRA_M2.mod");

        var picket = Picket.defaultValue();
        SonetImportManager sonetImportManager = SonetImportManager.create();
        picket = sonetImportManager.loadNameAndExperimentalDataFromEXPFile(file_exp, picket);
        picket = sonetImportManager.loadModelDataFromMODFile(file_mod, picket);
        return picket;
    }
}
