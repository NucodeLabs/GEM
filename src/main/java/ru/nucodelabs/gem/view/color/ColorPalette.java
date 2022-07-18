package ru.nucodelabs.gem.view.color;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;
import ru.nucodelabs.files.color_palette.ValueColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorPalette implements ColorMapper {

    private final List<ValueColor> valueColorList;
    private final DoubleProperty minValue = new SimpleDoubleProperty();
    private final DoubleProperty maxValue = new SimpleDoubleProperty();
    private final IntegerProperty blocksCount = new SimpleIntegerProperty();

    private final BooleanProperty logScaleProperty = new SimpleBooleanProperty(false);

    private final List<Segment> segmentList = new ArrayList<>();

    public ColorPalette(List<ValueColor> valueColorList, double minValue, double maxValue, int blocksCount) {
        this.valueColorList = valueColorList;
        setMinValue(minValue);
        setMaxValue(maxValue);
        setNumberOfSegments(blocksCount);
        if (blocksCount < 2) throw new RuntimeException("Число блоков меньше 2");
        blocksInit();
        this.blocksCount.addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() < 2) throw new RuntimeException("Число блоков меньше 2");
            segmentList.clear();
            blocksInit();
        });
        this.minValue.addListener((observable, oldValue, newValue) -> {
            segmentList.clear();
            blocksInit();
        });
        this.maxValue.addListener((observable, oldValue, newValue) -> {
            segmentList.clear();
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

        double blockSize = 1.0 / blocksCount.get();
        double currentFrom = 0;
        double currentTo = blockSize;

        //Первый блок
        segmentList.add(new Segment(currentFrom, blockSize, firstColor));
        currentFrom += blockSize;
        currentTo += blockSize;

        for (int i = 1; i < blocksCount.get() - 1; i++) {
            segmentList.add(new Segment(currentFrom, currentTo, blockColor(currentFrom, currentTo)));
            currentFrom += blockSize;
            currentTo += blockSize;
        }

        //Последний блок
        segmentList.add(new Segment(currentFrom, 1.0, lastColor));
    }

    private Segment blockFor(double percentage) {
        int low = 0;
        int high = segmentList.size() - 1;
        int mid;
        while (low <= high) {
            mid = low + (high - low) / 2;
            Segment block = segmentList.get(mid);
            if (block.getFrom() <= percentage && percentage <= block.getTo()) return block;
            else if (block.getTo() < percentage) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        throw new RuntimeException("Блок не найден бинарным поиском");
    }

    private double percentageFor(double resistance) {
        if (resistance < minValue.get()) return 0.0;
        if (resistance > maxValue.get()) return 1.0;
        return (resistance - minValue.get()) / (maxValue.get() - minValue.get());
    }

    @NotNull
    @Override
    public Color colorFor(double value) {
        double percentage = percentageFor(value);
        Segment block = blockFor(percentage);
        return block.getColor();
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
    public IntegerProperty numberOfSegmentsProperty() {
        return blocksCount;
    }

    @Override
    public int getNumberOfSegments() {
        return blocksCount.get();
    }

    @Override
    public void setNumberOfSegments(int value) {
        blocksCount.set(value);
    }

    @NotNull
    @Override
    public List<ColorMapper.Segment> getSegments() {
        return segmentList;
    }

    @NotNull
    @Override
    public BooleanProperty logScaleProperty() {
        return logScaleProperty;
    }

    @Override
    public boolean isLogScale() {
        return logScaleProperty.get();
    }

    @Override
    public void setLogScale(boolean value) {
        logScaleProperty.set(value);
    }
}

