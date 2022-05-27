package ru.nucodelabs.gem.view.color_palette;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import ru.nucodelabs.files.color_palette.CLRData;

public class ColorPalette {

    private final CLRData clrData;

    private final DoubleProperty minValue;

    private final DoubleProperty maxValue;

    public ColorPalette(CLRData clrData) {
        this.clrData = clrData;
        minValue = new SimpleDoubleProperty();
        maxValue = new SimpleDoubleProperty();
    }

    public Color colorForValue(double value) {
        double resistance = value - minValue.get();
        double topBorder = maxValue.get() - minValue.get();
        double percent;

        if (Double.isNaN(resistance / topBorder)) {
            percent = 100.0;
        } else {
            percent = (resistance / topBorder) * 100;
        }

        double lowerPoint = getNearestLowerPoint(percent);
        double upperPoint = getNearestUpperPoint(percent);

        double difference = upperPoint - lowerPoint;

        if (difference < 0) {
            return Color.rgb(
                    clrData.colorMap.get(0.0).red(),
                    clrData.colorMap.get(0.0).green(),
                    clrData.colorMap.get(0.0).blue());
        } else if (percent >= 100) {
            return Color.rgb(
                    clrData.colorMap.get(100.0).red(),
                    clrData.colorMap.get(100.0).green(),
                    clrData.colorMap.get(100.0).blue());
        }

        double partition = (percent - lowerPoint) / difference;
        int[] rgbo = getColors(partition, lowerPoint, upperPoint);

        return Color.rgb(
                rgbo[0],
                rgbo[1],
                rgbo[2],
                (double) rgbo[3] / 255);
    }

    private int[] getColors(double partition, double lowerPoint, double upperPoint) {
        int[] rgbo = new int[4];
        rgbo[0] = clrData.colorMap.get(lowerPoint).red() + (int) Math.floor(partition * (clrData.colorMap.get(upperPoint).red() - clrData.colorMap.get(lowerPoint).red()));
        rgbo[1] = clrData.colorMap.get(lowerPoint).green() + (int) Math.floor(partition * (clrData.colorMap.get(upperPoint).green() - clrData.colorMap.get(lowerPoint).green()));
        rgbo[2] = clrData.colorMap.get(lowerPoint).blue() + (int) Math.floor(partition * (clrData.colorMap.get(upperPoint).blue() - clrData.colorMap.get(lowerPoint).blue()));
        rgbo[3] = clrData.colorMap.get(lowerPoint).opacity() + (int) Math.floor(partition * (clrData.colorMap.get(upperPoint).opacity() - clrData.colorMap.get(lowerPoint).opacity()));

        return rgbo;
    }

    private Double getNearestLowerPoint(double percent) {
        double prevKey = 0.0;
        for (double key : clrData.colorMap.keySet()) {
            if (percent < key) {
                return prevKey;
            } else if (percent == key) {
                return key;
            } else if (percent > key) {
                prevKey = key;
            }
        }

        return prevKey;
    }

    private Double getNearestUpperPoint(double percent) {
        double nextKey = percent;
        for (double key : clrData.colorMap.keySet()) {
            if (percent <= key) {
                return key;
            }
        }

        return nextKey;
    }

    public CLRData getClrData() {
        return clrData;
    }

    public double getMinValue() {
        return minValue.get();
    }

    public void setMinValue(double minValue) {
        this.minValue.set(minValue);
    }

    public DoubleProperty minValueProperty() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue.get();
    }

    public void setMaxValue(double maxValue) {
        this.maxValue.set(maxValue);
    }

    public DoubleProperty maxValueProperty() {
        return maxValue;
    }
}
