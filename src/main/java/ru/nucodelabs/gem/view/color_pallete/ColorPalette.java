package ru.nucodelabs.gem.view.color_pallete;

import javafx.scene.paint.Color;

public class ColorPalette {
    public static String getRGBColor(double resistance) {
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
}
