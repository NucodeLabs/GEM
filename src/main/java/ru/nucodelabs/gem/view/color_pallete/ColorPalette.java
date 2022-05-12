package ru.nucodelabs.gem.view.color_pallete;

import javafx.scene.paint.Color;
import ru.nucodelabs.files.color_palette.CLRData;

public class ColorPalette {

    private CLRData clrData;

    public ColorPalette(CLRData clrData) {
        this.clrData = clrData;
    }

    public String getCLRColor(double resistance, double maxResistance) {
        double percent = (resistance / maxResistance) * 100;
        double lowerPoint = getNearestLowerPoint(percent);
        double upperPoint = getNearestUpperPoint(percent);

        double difference = upperPoint - lowerPoint;
        if (difference <= 0) {
            return String.format("%d, %d, %d %d",
                    clrData.colorMap.get(100.0).red(),
                    clrData.colorMap.get(100.0).green(),
                    clrData.colorMap.get(100.0).blue(),
                    clrData.colorMap.get(100.0).opacity());
        }

        double partition = (percent - lowerPoint) / (difference);
        int[] rgbo = getColors(partition, lowerPoint, upperPoint);

        return String.format("%d, %d, %d %d",
                rgbo[0],
                rgbo[1],
                rgbo[2],
                rgbo[3]);
    }

    private int[] getColors(double partition, double lowerPoint, double upperPoint) {
        int[] rgbo = new int[4];
        rgbo[0] = clrData.colorMap.get(lowerPoint).red() + (int) Math.floor(partition * (clrData.colorMap.get(upperPoint).red() - clrData.colorMap.get(lowerPoint).red()));
        rgbo[1] = clrData.colorMap.get(lowerPoint).green() + (int) Math.floor(partition * (clrData.colorMap.get(upperPoint).green() - clrData.colorMap.get(lowerPoint).green()));
        rgbo[2] = clrData.colorMap.get(lowerPoint).blue() + (int) Math.floor(partition * (clrData.colorMap.get(upperPoint).blue() - clrData.colorMap.get(lowerPoint).blue()));
        rgbo[3] = clrData.colorMap.get(lowerPoint).opacity() + (int) Math.floor(partition * (clrData.colorMap.get(upperPoint).opacity() - clrData.colorMap.get(lowerPoint).opacity()));

        return rgbo;
    }

    public String getRGBColor(double resistance) {
        Color color = Color.WHITESMOKE;

        if (0 < resistance & resistance < 20) {
            color = Color.LIGHTGREEN;
        } else if (20 <= resistance & resistance < 50) {
            color = Color.GREEN;
        } else if (50 <= resistance & resistance < 100) {
            color = Color.OLIVE;
        } else if (100 <= resistance & resistance < 150) {
            color = Color.DARKGREEN;
        } else if (150 <= resistance & resistance < 200) {
            color = Color.GREENYELLOW;
        } else if (200 <= resistance & resistance < 250) {
            color = Color.LIGHTYELLOW;
        } else if (250 <= resistance & resistance < 300) {
            color = Color.YELLOW;
        } else if (300 <= resistance & resistance < 350) {
            color = Color.DARKGOLDENROD;
        } else if (350 <= resistance & resistance < 400) {
            color = Color.ORANGE;
        } else if (400 <= resistance & resistance < 450) {
            color = Color.DARKORANGE;
        } else if (450 <= resistance & resistance < 500) {
            color = Color.ORANGERED;
        } else if (500 <= resistance & resistance < 750) {
            color = Color.RED;
        } else if (750 <= resistance & resistance < 1000) {
            color = Color.DARKRED;
        } else if (1000 <= resistance) {
            color = Color.DARKGRAY;
        }

        return String.format("%d, %d, %d",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
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
        double nextKey = 0.0;
        for (double key : clrData.colorMap.keySet()) {
            if (percent < key) {
                nextKey = key;
            } else if (percent >= key) {
                return nextKey;
            }
        }

        return nextKey;
    }
}
