package ru.nucodelabs.files.color_palette;

import javafx.scene.paint.Color;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class ClrParser {
    private String name = "default";
    private final List<ValueColor> valueColorList = new ArrayList<>();

    public List<ValueColor> parse(InputStream clrFile) {
        Scanner sc = new Scanner(clrFile).useLocale(Locale.US);
        name = sc.nextLine();

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

    public List<ValueColor> getValueColors() {
        return valueColorList;
    }

    public String getName() {
        return name;
    }
}