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
    void readEXP_test() throws FileNotFoundException {
        File file = new File("data/BURM1.EXP");
        EXPFile exp = Sonet.readEXP(file);
        System.out.println(exp.getSTTFileName());
        System.out.println(exp.getNumber());
        System.out.println(exp.getDate());
        System.out.println(exp.getWeather());
        System.out.println(exp.getOperator());
        System.out.println(exp.getInterpreter());
        System.out.println(exp.getChecked());
        for (int i = 0; i < exp.getResistanceApp().size(); i++) {
            System.out.println(
                    exp.getAmperage().get(i).toString() + "   "
                            + exp.getVoltage().get(i).toString() + "   "
                            + exp.getResistanceApp().get(i).toString() + "   "
                            + exp.getErrorResistanceApp().get(i).toString() + "   "
                            + exp.getPolarizationApp().get(i).toString() + "   "
                            + exp.getErrorPolarizationApp().get(i).toString()
            );
        }
    }
}
