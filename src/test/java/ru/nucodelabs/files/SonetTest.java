package ru.nucodelabs.files;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.STTFile;
import ru.nucodelabs.files.sonet.Sonet;

import java.io.File;
import java.io.FileNotFoundException;

public class SonetTest {
    @Test
    void test_CHITA_STT() throws FileNotFoundException {
        File file = new File("data/CHITA.STT");
        STTFile stt = Sonet.readSTT(file);
        Assertions.assertEquals(stt.getAB_2().get(20), 1.5e+003);
        for (int i = 0; i < stt.getAB_2().size(); i++) {
            System.out.println(
                    stt.getAB_2().get(i).toString() + " "
                            + stt.getMN_2().get(i).toString());
        }
    }

    @Test
    void readEXP_BURM_EXP() throws FileNotFoundException {
        File file = new File("data/BURM1.EXP");
        EXPFile exp = Sonet.readEXP(file);
    }
}
