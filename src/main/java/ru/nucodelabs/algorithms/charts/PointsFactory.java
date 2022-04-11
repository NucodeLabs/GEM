package ru.nucodelabs.algorithms.charts;

import java.util.List;

interface PointsFactory {

    /**
     * Возвращает новые точки для графика
     *
     * @return новые точки
     */
    List<Point> points();

    /**
     * Возвращает новые точки для графика с прологарифмированными с основанием 10 значениями
     *
     * @return новые точки
     */
    List<Point> log10Points();

    /**
     * Возвращает новые точки для графика с прологарифмированными с основанием {@code a} значениями
     *
     * @param logBase основание логарифма
     * @return новые точки
     */
    default List<Point> logPoints(int logBase) {
        throw new UnsupportedOperationException();
    }
}
