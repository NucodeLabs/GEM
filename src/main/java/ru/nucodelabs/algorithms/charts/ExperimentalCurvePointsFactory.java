package ru.nucodelabs.algorithms.charts;

import ru.nucodelabs.data.ves.ExperimentalData;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.log10;
import static java.lang.Math.max;

public final class ExperimentalCurvePointsFactory implements PointsFactory {

    private final ExperimentalData experimentalData;

    ExperimentalCurvePointsFactory(ExperimentalData experimentalData) {
        this.experimentalData = experimentalData;
    }

    @Override
    public List<Point> points() {
        if (experimentalData.size() == 0) {
            return new ArrayList<>();
        }

        List<Point> points = new ArrayList<>();

        for (int i = 0; i < experimentalData.size(); i++) {
            double dotX = experimentalData.ab_2().get(i);
            double dotY = max(experimentalData.resistanceApparent().get(i), 0);

            points.add(new Point(dotX, dotY));
        }

        return points;
    }

    @Override
    public List<Point> log10Points() {
        if (experimentalData.size() == 0) {
            return new ArrayList<>();
        }

        List<Point> points = new ArrayList<>();

        for (int i = 0; i < experimentalData.size(); i++) {
            double dotX = log10(experimentalData.ab_2().get(i));
            double dotY = max(log10(experimentalData.resistanceApparent().get(i)), 0);

            points.add(new Point(dotX, dotY));
        }

        return points;
    }

    // +++++++++++++++++++++++++++++++++++++++ ERROR POINTS +++++++++++++++++++++++++++++++++++++++++++++++
    private enum BoundType {UPPER_BOUND, LOWER_BOUND}

    static final class UpperBoundErrorPointsFactory implements PointsFactory {

        private final ExperimentalData experimentalData;

        UpperBoundErrorPointsFactory(ExperimentalData experimentalData) {
            this.experimentalData = experimentalData;
        }

        @Override
        public List<Point> points() {
            return new ErrorPointsFactory(BoundType.UPPER_BOUND, experimentalData).points();
        }

        @Override
        public List<Point> log10Points() {
            return new ErrorPointsFactory(BoundType.UPPER_BOUND, experimentalData).log10Points();
        }
    }

    static final class LowerBoundErrorPointsFactory implements PointsFactory {

        private final ExperimentalData experimentalData;

        LowerBoundErrorPointsFactory(ExperimentalData experimentalData) {
            this.experimentalData = experimentalData;
        }

        @Override
        public List<Point> points() {
            return new ErrorPointsFactory(BoundType.LOWER_BOUND, experimentalData).points();
        }

        @Override
        public List<Point> log10Points() {
            return new ErrorPointsFactory(BoundType.LOWER_BOUND, experimentalData).log10Points();
        }
    }

    private static final class ErrorPointsFactory implements PointsFactory {
        private final BoundType boundType;
        private final ExperimentalData experimentalData;

        ErrorPointsFactory(BoundType boundType, ExperimentalData experimentalData) {
            this.boundType = boundType;
            this.experimentalData = experimentalData;
        }

        @Override
        public List<Point> points() {
            if (experimentalData.size() == 0) {
                return new ArrayList<>();
            }

            List<Point> points = new ArrayList<>();

            for (int i = 0; i < experimentalData.size(); i++) {
                double dotX = log10(experimentalData.ab_2().get(i));
                double error = experimentalData.errorResistanceApparent().get(i) / 100f;
                double dotY;
                if (boundType == BoundType.UPPER_BOUND) {
                    dotY = max(
                            experimentalData.resistanceApparent().get(i)
                                    + experimentalData.resistanceApparent().get(i) * error, 0);
                } else {
                    dotY = max(
                            experimentalData.resistanceApparent().get(i)
                                    - experimentalData.resistanceApparent().get(i) * error, 0);
                }

                points.add(new Point(dotX, dotY));
            }

            return points;
        }

        @Override
        public List<Point> log10Points() {
            if (experimentalData.size() == 0) {
                return new ArrayList<>();
            }

            List<Point> points = new ArrayList<>();

            for (int i = 0; i < experimentalData.size(); i++) {
                double dotX = log10(experimentalData.ab_2().get(i));
                double error = experimentalData.errorResistanceApparent().get(i) / 100f;
                double dotY;
                if (boundType == BoundType.UPPER_BOUND) {
                    dotY = max(
                            log10(experimentalData.resistanceApparent().get(i)
                                    + experimentalData.resistanceApparent().get(i) * error), 0);
                } else {
                    dotY = max(
                            log10(experimentalData.resistanceApparent().get(i)
                                    - experimentalData.resistanceApparent().get(i) * error), 0);
                }

                points.add(new Point(dotX, dotY));
            }

            return points;
        }
    }
}
