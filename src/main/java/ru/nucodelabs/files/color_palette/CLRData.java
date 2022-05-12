package ru.nucodelabs.files.color_palette;

import java.util.HashMap;

public class CLRData {
    public HashMap<Double, ColorSettings> colorMap;

    public CLRData() {
        colorMap = new HashMap<>();
    }

    public record ColorSettings(int red, int green, int blue, int opacity) {

        @Override
        public int red() {
            return red;
        }

        @Override
        public int green() {
            return green;
        }

        @Override
        public int blue() {
            return blue;
        }

        @Override
        public int opacity() {
            return opacity;
        }
    }
}
