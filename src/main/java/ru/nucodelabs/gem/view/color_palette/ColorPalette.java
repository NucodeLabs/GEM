package ru.nucodelabs.gem.view.color_palette;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;
import ru.nucodelabs.files.color_palette.ValueColor;
import ru.nucodelabs.gem.view.color.ColorMapper;

import java.util.*;

public class ColorPalette implements ColorMapper {

    private final List<ValueColor> valueColorList;
    private final DoubleProperty minValue;
    private final DoubleProperty maxValue;
    private final IntegerProperty blocksCnt;

    private final List<ColorBlock> colorBlockList = new ArrayList<>();

    public ColorPalette(List<ValueColor> valueColorList, DoubleProperty minValue, DoubleProperty maxValue, IntegerProperty blocksCnt) {
        this.valueColorList = valueColorList;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.blocksCnt = blocksCnt;
        if (blocksCnt.get() < 2) throw new RuntimeException("Число блоков меньше 2");
        blocksInit();
        blocksCnt.addListener((observable, oldValue, newValue) -> {
            colorBlockList.clear();
            if (blocksCnt.get() < 2) throw new RuntimeException("Число блоков меньше 2");
            blocksInit();
        });
    }

    /**
     *
     * @param c1 Первый цвет
     * @param c2 Второй цвет
     * @param percentage Процент отступа от c1
     * @return Возвращает цвет
     */
    private Color colorInterpolate(Color c1, Color c2, double percentage) {
        double r1 = c1.getRed();
        double g1 = c1.getGreen();
        double b1 = c1.getBlue();

        double r2 = c2.getRed();
        double g2 = c2.getGreen();
        double b2 = c2.getBlue();

        double r = r1 + (r2 - r1) * percentage;
        double g = g1 + (g2 - g1) * percentage;
        double b = b1 + (b2 - b1) * percentage;
        double a = 1.0;

        return new Color(r, g, b, a);
    }

    /**
     * Ищет цвет в точке интерполяции между vc1 и vc2
     * @param vc1 vc1.percentage() < vc2.percentage()
     * @param vc2 vc2.percentage() > vc1.percentage()
     * @param percentage vc1.percentage() < percentage < vc2.percentage()
     * @return Interpolated rgba color between vc1.color() and vc2.color()
     */
    private Color vcInterpolate(ValueColor vc1, ValueColor vc2, double percentage) {
        Color c1 = vc1.color();
        Color c2 = vc2.color();
        double diff = (percentage - vc1.percentage()) / (vc2.percentage() - vc1.percentage());

        return colorInterpolate(c1, c2, diff);
    }

    private Color blockColor(double from, double to) {
        List<ValueColor> vcsFrom = findNearestVCs(from);
        List<ValueColor> vcsTo = findNearestVCs(to);

        Color colorFrom = vcInterpolate(vcsFrom.get(0), vcsFrom.get(1), from);
        Color colorTo = vcInterpolate(vcsTo.get(0), vcsTo.get(1), to);

        return colorInterpolate(colorFrom, colorTo, 0.5);
    }

    private List<ValueColor> findNearestVCs(double percentage) {
        for (int i = 0; i < valueColorList.size() - 1; i++) {
            double vcPercentage1 = valueColorList.get(i).percentage();
            double vcPercentage2 = valueColorList.get(i + 1).percentage();
            if (vcPercentage1 <= percentage && vcPercentage2 >= percentage)
                return new ArrayList<>(Arrays.asList(valueColorList.get(i), valueColorList.get(i + 1)));
        }
        throw new RuntimeException("Цвет не найден");
    }

    private void blocksInit() {
        Color firstColor = valueColorList.get(0).color();
        Color lastColor = valueColorList.get(valueColorList.size() - 1).color();

        double blockSize = 1.0 / blocksCnt.get();
        double currentFrom = 0;
        double currentTo = blockSize;

        //Первый блок
        colorBlockList.add(new ColorBlock(currentFrom, blockSize, firstColor));
        currentFrom += blockSize;
        currentTo += blockSize;

        for (int i = 1; i < blocksCnt.get() - 1; i++) {
            colorBlockList.add(new ColorBlock(currentFrom, currentTo, blockColor(currentFrom, currentTo)));
            currentFrom += blockSize;
            currentTo += blockSize;
        }

        //Последний блок
        colorBlockList.add(new ColorBlock(currentFrom, 1.0, lastColor));
    }

    private ColorBlock blockFor(double percentage) {
        int low = 0;
        int high = colorBlockList.size() - 1;
        int mid;
        while (low <= high) {
            mid = low + (high - low) / 2;
            ColorBlock block = colorBlockList.get(mid);
            if (block.from() <= percentage && percentage <= block.to()) return block;
            else if (block.to() < percentage) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        throw new RuntimeException("Блок не найден бинарным поиском");
    }

    private double percentageFor(double resistance) {
        return (resistance - minValue.get()) / (maxValue.get() - minValue.get());
    }

    @NotNull
    @Override
    public Color colorFor(double value) {
        double percentage = percentageFor(value);
        ColorBlock block = blockFor(percentage);
        return block.color();
    }

    @Override
    public double getMinValue() {
        return minValue.get();
    }

    @Override
    public void setMinValue(double value) {
        this.minValue.set(value);
    }

    @Override
    public double getMaxValue() {
        return maxValue.get();
    }

    @Override
    public void setMaxValue(double value) {
        this.maxValue.set(value);
    }

    @NotNull
    @Override
    public DoubleProperty minValueProperty() {
        return minValue;
    }

    @NotNull
    @Override
    public DoubleProperty maxValueProperty() {
        return maxValue;
    }

    @NotNull
    @Override
    public IntegerProperty blocksCntProperty() {
        return blocksCnt;
    }

    @Override
    public int getBlocksCnt() {
        return blocksCnt.get();
    }

    @Override
    public void setBlocksCnt(int value) {
        blocksCnt.set(value);
    }

    @NotNull
    @Override
    public List<ColorBlock> getColorBlockList() {
        return colorBlockList;
    }

    public record ColorBlock(double from, double to, Color color) {

    }
}

