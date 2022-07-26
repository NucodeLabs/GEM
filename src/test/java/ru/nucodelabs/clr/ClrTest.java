package ru.nucodelabs.clr;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import ru.nucodelabs.files.clr.ClrParser;
import ru.nucodelabs.files.clr.ColorNode;
import ru.nucodelabs.gem.view.color.ColorPalette;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClrTest {

    @Test
    public void test_parser() throws FileNotFoundException {
        File file = new File("colormap/default.clr");
        ClrParser clrParser = new ClrParser(file);

        List<ColorNode> valueColorList = clrParser.getColorNodes();

        Comparator<ColorNode> c = Comparator.comparing(ColorNode::getPosition);

        int vcIndex = Collections.binarySearch(valueColorList, new ColorNode(0.40369441, Color.BLACK), c);

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
        File file = new File("colormap/default.clr");
        ClrParser clrParser = new ClrParser(file);

        List<ColorNode> valueColorList = clrParser.getColorNodes();
        ColorPalette colorPalette = new ColorPalette(valueColorList, 0, 1500, 10);
        Color color = colorPalette.colorFor(1200);
    }
}
