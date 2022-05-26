package ru.nucodelabs.gem.view.color_palette;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import ru.nucodelabs.files.color_palette.CLRData;

public class ColorPalette {

    private final CLRData clrData;

    public DoubleProperty minResistanceProperty;

    public DoubleProperty maxResistanceProperty;

    public ColorPalette(CLRData clrData) {
        this.clrData = clrData;
        minResistanceProperty = new SimpleDoubleProperty(0.0);
        maxResistanceProperty = new SimpleDoubleProperty(1500.0);
    }

    public String getCLRColor(double currentResistance) {
        double resistance = currentResistance - minResistanceProperty.get();
        double topBorder = maxResistanceProperty.get() - minResistanceProperty.get();

        double percent = (resistance / topBorder) * 100;
        double lowerPoint = getNearestLowerPoint(percent);
        double upperPoint = getNearestUpperPoint(percent);

        double difference = upperPoint - lowerPoint;
        if (difference < 0) {
            return String.format("%d, %d, %d, %f",
                    clrData.colorMap.get(0.0).red(),
                    clrData.colorMap.get(0.0).green(),
                    clrData.colorMap.get(0.0).blue(),
                    1.0);
        } else if (percent >= 100) {
            return String.format("%d, %d, %d, %f",
                    clrData.colorMap.get(100.0).red(),
                    clrData.colorMap.get(100.0).green(),
                    clrData.colorMap.get(100.0).blue(),
                    1.0);
        }

        double partition = (percent - lowerPoint) / (difference);
        int[] rgbo = getColors(partition, lowerPoint, upperPoint);

        return String.format("%d, %d, %d, %f",
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
}
