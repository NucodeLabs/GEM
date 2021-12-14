package ru.nucodelabs.files;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.MODFile;
import ru.nucodelabs.files.sonet.STTFile;
import ru.nucodelabs.files.sonet.SonetImport;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.function.Function;

public class SonetImportTest {
    Function<Double, String> checkNull = d -> {
        if (d != null)
            return d.toString();
        else
            return "";
    };

    @Test
    void readSTT_test() throws FileNotFoundException {
        System.out.println("SonetTest.readSTT_test");
        File file = new File("data/CHITA.STT");
        STTFile stt = SonetImport.readSTT(file);
        Assertions.assertEquals(stt.getAB_2().get(20), 1.5e+003);
        for (int i = 0; i < stt.getAB_2().size(); i++) {
            System.out.println(
                    checkNull.apply(stt.getAB_2().get(i)) + "   "
                            + checkNull.apply(stt.getMN_2().get(i)));
        }
    }

    @Test
    void readEXP_test() throws FileNotFoundException {
        System.out.println("SonetTest.readEXP_test");
        File file = new File("data/BURM1.EXP");
        EXPFile exp = SonetImport.readEXP(file);
        System.out.println(exp.getSTTFileName());
        System.out.println(exp.getNumber());
        System.out.println(exp.getDate());
        System.out.println(exp.getWeather());
        System.out.println(exp.getOperator());
        System.out.println(exp.getInterpreter());
        System.out.println(exp.getChecked());
        for (int i = 0; i < exp.getResistanceApparent().size(); i++) {
            System.out.println(
                    checkNull.apply(exp.getAmperage().get(i)) + "   "
                            + checkNull.apply(exp.getVoltage().get(i)) + "   "
                            + checkNull.apply(exp.getResistanceApparent().get(i)) + "   "
                            + checkNull.apply(exp.getErrorResistanceApparent().get(i)) + "   "
                            + checkNull.apply(exp.getPolarizationApparent().get(i)) + "   "
                            + checkNull.apply(exp.getErrorPolarizationApparent().get(i))
            );
        }
        Path openedFilePath = file.toPath();
        STTFile stt = SonetImport.readSTT(new File(
                openedFilePath.getParent().toString()
                        + File.separator
                        + exp.getSTTFileName()));
        System.out.println(" =========== " + exp.getSTTFileName() + " =========== ");
        for (int i = 0; i < stt.getAB_2().size(); i++) {
            System.out.println(
                    checkNull.apply(stt.getAB_2().get(i)) + "   "
                            + checkNull.apply(stt.getMN_2().get(i)));
        }
    }

    @Test
    void readMOD_test() throws FileNotFoundException {
        System.out.println("SonetTest.readMOD_test");
        File file = new File("data/KAZAN.MOD");
        MODFile mod = SonetImport.readMOD(file);
        for (int i = 0; i < mod.getResistance().size(); i++) {
            System.out.println(
                    checkNull.apply(mod.getResistance().get(i)) + "   "
                            + checkNull.apply(mod.getPower().get(i)) + "   "
                            + checkNull.apply(mod.getPolarization().get(i))
            );
        }
    }

    @Test
    void readEXP_test1() throws FileNotFoundException {
        System.out.println("SonetTest.readEXP_test");
        File file = new File("data/BURM4.EXP");
        EXPFile exp = SonetImport.readEXP(file);
        System.out.println("SonetTest.readEXP_test");
    }
}