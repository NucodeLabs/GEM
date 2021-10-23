package ru.nucodelabs.files;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nucodelabs.files.sonet.STTFile;
import ru.nucodelabs.files.sonet.Sonet;

import java.io.File;
import java.io.FileNotFoundException;

public class SonetTest {
    @Test
    void test_CHITA_STT() throws FileNotFoundException {
        File file = new File("src/test/resources/CHITA.STT");
        STTFile stt = Sonet.readSTT(file);
        Assertions.assertEquals(stt.getAB_2().get(20), 1.5e+003);
    }
}
