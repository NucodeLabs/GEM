package ru.nucodelabs.files.color_palette;

import java.util.LinkedHashMap;
import java.util.Map;
@Deprecated
public class CLRData {
    private Map<Double, ColorSettings> colorMap;

    public CLRData() {
        colorMap = new LinkedHashMap<>();
    }

    public record ColorSettings(int red, int green, int blue, int opacity) {

    }

    public Map<Double, ColorSettings> getColorMap() {
        return colorMap;
    }

    public void setColorMap(Map<Double, ColorSettings> colorMap) {
        this.colorMap = colorMap;
    }}
