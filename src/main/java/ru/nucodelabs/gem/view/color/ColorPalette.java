package ru.nucodelabs.gem.view.color;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;
import ru.nucodelabs.files.clr.ColorNode;
import ru.nucodelabs.gem.util.std.MathKt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ColorPalette implements ColorMapper {

    private final List<ColorNode> valueColorList;
    private final DoubleProperty minValueProperty = new SimpleDoubleProperty();
    private Double minValue;
    private final DoubleProperty maxValueProperty = new SimpleDoubleProperty();
    private Double maxValue;
    private final IntegerProperty blocksCount = new SimpleIntegerProperty();

    private final BooleanProperty logScaleProperty = new SimpleBooleanProperty(false);

    private final List<Segment> segmentList = new ArrayList<>();

    public ColorPalette(List<ColorNode> valueColorList, double minValue, double maxValue, int blocksCount) {
        this.valueColorList = valueColorList;
        setMinValue(minValue);
        setMaxValue(maxValue);
        setNumberOfSegments(blocksCount);
        if (blocksCount < 2) throw new RuntimeException("Число блоков меньше 2");
        checkLog();
        blocksInit();
        this.logScaleProperty.addListener((observable, oldValue, newValue) -> {
            segmentList.clear();
            checkLog();
            blocksInit();
        });
        this.blocksCount.addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() < 2) throw new RuntimeException("Число блоков меньше 2");
            segmentList.clear();
            checkLog();
            blocksInit();
        });
        this.minValueProperty.addListener((observable, oldValue, newValue) -> {
            segmentList.clear();
            checkLog();
            blocksInit();
        });
        this.maxValueProperty.addListener((observable, oldValue, newValue) -> {
            segmentList.clear();
            checkLog();
            blocksInit();
        });
    }

    /**
     * @param c1         Первый цвет
     * @param c2         Второй цвет
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
     *
     * @param vc1        vc1.position() < vc2.position()
     * @param vc2        vc2.position() > vc1.position()
     * @param percentage vc1.position() < position < vc2.position()
     * @return Interpolated rgba color between vc1.color() and vc2.color()
     */
    private Color vcInterpolate(ColorNode vc1, ColorNode vc2, double percentage) {
        Color c1 = vc1.getColor();
        Color c2 = vc2.getColor();
        double diff = (percentage - vc1.getPosition()) / (vc2.getPosition() - vc1.getPosition());

        return colorInterpolate(c1, c2, diff);
    }

    private Color blockColor(double from, double to) {
        List<ColorNode> vcsFrom = findNearestVCs(from);
        List<ColorNode> vcsTo = findNearestVCs(to);

        Color colorFrom = vcInterpolate(vcsFrom.get(0), vcsFrom.get(1), from);
        Color colorTo = vcInterpolate(vcsTo.get(0), vcsTo.get(1), to);

        return colorInterpolate(colorFrom, colorTo, 0.5);
    }

    private List<ColorNode> findNearestVCs(double percentage) {
        for (int i = 0; i < valueColorList.size() - 1; i++) {
            double vcPercentage1 = valueColorList.get(i).getPosition();
            double vcPercentage2 = valueColorList.get(i + 1).getPosition();
            if (vcPercentage1 <= percentage && vcPercentage2 >= percentage)
                return new ArrayList<>(Arrays.asList(valueColorList.get(i), valueColorList.get(i + 1)));
        }
        throw new RuntimeException("Цвет не найден");
    }

    private void blocksInit() {
        Color firstColor = valueColorList.get(0).getColor();
        Color lastColor = valueColorList.get(valueColorList.size() - 1).getColor();

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
        if (percentage == 1.0) return segmentList.get(segmentList.size() - 1);
        return segmentList.get((int) Math.floor(percentage / (1.0 / segmentList.size())));
    }

    private double percentageFor(double resistance) {
        if (resistance < minValue) return 0.0;
        if (resistance > maxValue) return 1.0;
        return (resistance - minValue) / (maxValue - minValue);
    }

    private void checkLog() {
        if (minValueProperty.get() < 0.1) minValue = 0.1;
        if (logScaleProperty.get()) {
            minValue = Math.log10(minValueProperty.get());
            maxValue = Math.log10(maxValueProperty.get());
        } else {
            minValue = minValueProperty.get();
            maxValue = maxValueProperty.get();
        }
    }

    @NotNull
    @Override
    public Color colorFor(double value) {
        double percentage;
        if (logScaleProperty.get()) percentage = percentageFor(Math.log10(value));
        else percentage = percentageFor(value);
        Segment block = blockFor(percentage);
        return block.getColor();
    }

    @Override
    public double getMinValue() {
        return minValueProperty.get();
    }

    @Override
    public void setMinValue(double value) {
        this.minValueProperty.set(value);
    }

    @Override
    public double getMaxValue() {
        return maxValueProperty.get();
    }

    @Override
    public void setMaxValue(double value) {
        this.maxValueProperty.set(value);
    }

    @NotNull
    @Override
    public DoubleProperty minValueProperty() {
        return minValueProperty;
    }

    @NotNull
    @Override
    public DoubleProperty maxValueProperty() {
        return maxValueProperty;
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
        if (!logScaleProperty.get()) return segmentList
                .stream()
                .map(s -> new Segment(
                        s.getFrom() * (maxValue - minValue) + minValue,
                        s.getTo() * (maxValue - minValue) + minValue,
                        s.getColor()
                )).collect(Collectors.toList());
        else return segmentList
                .stream()
                .map(s -> new Segment(
                        logValue(valueFor(s.getFrom(), getMinValue(), getMaxValue()), getMinValue(), getMaxValue()),
                        logValue(valueFor(s.getTo(), getMinValue(), getMaxValue()), getMinValue(), getMaxValue()),
                        s.getColor()
                )).collect(Collectors.toList());
    }

    private Double valueFor(double percentage, double minValue, double maxValue) {
        return percentage * (maxValue - minValue) + minValue;
    }

    private Double logValue(double value, double minValue, double maxValue) {
        double logRange = Math.log10(maxValue) - Math.log10(minValue);
        double range = maxValue - minValue;
        double rDiv = range / logRange;
        double logValue = (value - minValue) / rDiv + Math.log10(minValue);
        return MathKt.exp10(logValue);
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

