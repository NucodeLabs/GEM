package ru.nucodelabs.clr;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import ru.nucodelabs.files.color_palette.ClrParser;
import ru.nucodelabs.files.color_palette.ValueColor;
import ru.nucodelabs.gem.view.color_palette.ColorPalette;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClrTest {

    @Test
    public void test_parser() throws FileNotFoundException {
        ClrParser clrParser = new ClrParser();
        File file = new File("data/clr/002_ERT_Rainbow_2.clr");
        InputStream inputStream = new FileInputStream(file);

        List<ValueColor> valueColorList = clrParser.parse(inputStream);

        Comparator<ValueColor> c = Comparator.comparing(ValueColor::percentage);

        int vcIndex = Collections.binarySearch(valueColorList, new ValueColor(40.369441, null), c);

        System.out.println(valueColorList.get(vcIndex).toString());
    }

//    @Test
//    public void blockFor_Test() {
//        ColorPalette colorPalette = new ColorPalette(null, null, null,4);
//        colorPalette.colorBlockList.add(new ColorPalette.ColorBlock(0, 0.25, Color.RED));
//        colorPalette.colorBlockList.add(new ColorPalette.ColorBlock(0.25, 0.5, Color.BLACK));
//        colorPalette.colorBlockList.add(new ColorPalette.ColorBlock(0.5, 0.75, Color.GREEN));
//        colorPalette.colorBlockList.add(new ColorPalette.ColorBlock(0.75, 1, Color.BLUE));
//
//        ColorPalette.ColorBlock cb = colorPalette.blockFor(0.4);
//    }

    @Test
    public void colorFor_test() throws FileNotFoundException {
        ClrParser clrParser = new ClrParser();
        File file = new File("data/clr/002_ERT_Rainbow_2.clr");
        InputStream inputStream = new FileInputStream(file);

        List<ValueColor> valueColorList = clrParser.parse(inputStream);
        ColorPalette colorPalette = new ColorPalette(valueColorList, 0, 1500, 10);
        Color color = colorPalette.colorFor(1200);
    }
}
