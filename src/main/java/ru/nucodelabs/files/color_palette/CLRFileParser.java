package ru.nucodelabs.files.color_palette;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CLRFileParser {
    private final File clrFile;

    public CLRFileParser(File clrFile) {
        this.clrFile = clrFile;
    }

    public CLRData parse() throws Exception {
        CLRData clrData = new CLRData();
        Scanner scanner = new Scanner(clrFile);
        int version = 0;

        double key;
        int red;
        int green;
        int blue;
        int opacity = 0;

        do {
            String str = scanner.nextLine();
            if (str.isBlank()) {
                continue;
            }

            if (str.equals("ColorMap 1 1")) {
                version = 1;
            } else if (str.equals("ColorMap 2 1")) {
                version = 2;
            } else if (str.contains("ColorMap 3")) {
                throw new Exception("Wrong CLR file type!");
            } else {
                List<String> lineList = Arrays.stream(str.split(" ")).toList();
                key = Double.parseDouble(lineList.get(0));
                red = Integer.parseInt(lineList.get(1));
                green = Integer.parseInt(lineList.get(2));
                blue = Integer.parseInt(lineList.get(3));

                if (version == 1) {
                    opacity = 255;
                } else if (version == 2) {
                    opacity = Integer.parseInt(lineList.get(4));
                }

                clrData.colorMap.put(key, new CLRData.ColorSettings(red, green, blue, opacity));
            }
        } while (scanner.hasNextLine());

        return clrData;
    }
}
