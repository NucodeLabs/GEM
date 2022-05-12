package ru.nucodelabs.files.color_palette;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CLRData {
    public Map<Double, ColorSettings> colorMap;

    public CLRData() {
        colorMap = new LinkedHashMap<>();
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
