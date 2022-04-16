package ru.nucodelabs.algorithms.charts;

import java.util.List;

@FunctionalInterface
public interface PointsConverter {
    /**
     * Возвращает новые точки для графика
     *
     * @return новые точки
     */
    List<Point> points();
}
