package ru.nucodelabs.files.color_palette;

import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import org.jetbrains.annotations.NotNull;
import ru.nucodelabs.gem.view.color.ColorMapper;

import java.util.ArrayList;
import java.util.List;

public class ColorPalette implements ColorMapper {

    private final List<ValueColor> valueColorList;
    private final DoubleProperty minValue;
    private final DoubleProperty maxValue;
    private final int blocksCnt;

    ColorPalette(List<ValueColor> valueColorList, DoubleProperty minValue, DoubleProperty maxValue, int blocksCnt) {
        this.valueColorList = valueColorList;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.blocksCnt = blocksCnt;
    }

    private LinearGradient gradient() {
        Stop[] stops = new Stop[valueColorList.size()];

        for (int i = 0; i < valueColorList.size(); i++) {
            stops[i] = new Stop(valueColorList.get(i).percentage(), valueColorList.get(i).color());
        }

        return new LinearGradient(0, 0, 1, 0, false, CycleMethod.NO_CYCLE, stops);
    }

//    private List<ValueColor> discreteColors() {
//        LinearGradient lg = gradient();
//        List<ValueColor> discreteValueColorList = new ArrayList<>();
//
//        for (int i = 0; i < steps; i++) {
//            Color color = lg.
//        }
//    }


    @NotNull
    @Override
    public Color colorFor(double value) {
        return null;
    }

    @Override
    public double getMinValue() {
        return 0;
    }

    @Override
    public void setMinValue(double value) {

    }

    @Override
    public double getMaxValue() {
        return 0;
    }

    @Override
    public void setMaxValue(double value) {

    }

    @NotNull
    @Override
    public DoubleProperty minValueProperty() {
        return null;
    }

    @NotNull
    @Override
    public DoubleProperty maxValueProperty() {
        return null;
    }
}
