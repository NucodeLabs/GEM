package ru.nucodelabs.files.color_palette;

import javafx.scene.paint.Color;

import java.io.InputStream;
import java.util.*;

public class CLRParser {
    private String nameCLR = "default";
    private final List<ValueColor> valueColorList = new ArrayList<>();

    public List<ValueColor> parseCLR(InputStream clrFile) {
        Scanner sc = new Scanner(clrFile).useLocale(Locale.US);
        nameCLR = sc.nextLine();

        while (sc.hasNextLine()) {
            double percentage = sc.nextDouble() * 0.01;

            int red = sc.nextInt();
            int green = sc.nextInt();
            int blue = sc.nextInt();
            double alpha = (double) sc.nextInt() / 255.0;
            Color color = Color.rgb(red, green, blue, alpha);

            valueColorList.add(new ValueColor(percentage, color));

            sc.nextLine();
        }

        return valueColorList;
    }

    public List<ValueColor> getValueColorList() {
        return valueColorList;
    }

    public String getNameCLR() {
        return nameCLR;
    }
}