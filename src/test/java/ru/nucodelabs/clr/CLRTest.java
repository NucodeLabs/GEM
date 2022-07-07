package ru.nucodelabs.clr;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import ru.nucodelabs.files.color_palette.CLRParser;
import ru.nucodelabs.files.color_palette.ValueColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class CLRTest {

    @Test
    public void test_parser() throws FileNotFoundException {
        CLRParser clrParser = new CLRParser();
        File file = new File("data/clr/002_ERT_Rainbow_2.clr");
        InputStream inputStream = new FileInputStream(file);

        List<ValueColor> valueColorList = clrParser.parseCLR(inputStream);

        Comparator<ValueColor> c = Comparator.comparing(ValueColor::percentage);

        int vcIndex = Collections.binarySearch(valueColorList, new ValueColor(40.369441, null), c);

        System.out.println(valueColorList.get(vcIndex).toString());
    }
}
